package com.amazon.example;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;

import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import org.xml.sax.SAXException;

import com.amazon.example.models.YarnApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;



public class YarnUtils {
	private Logger logger = LoggerFactory.getLogger(YarnUtils.class);

	class XmlHandlerClass<T> implements HttpClientResponseHandler<Document> {
		@Override
		public Document handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
			{
				int statusLine = response.getCode();
				HttpEntity entity = response.getEntity();
				if (statusLine >= 300) {
					throw new HttpResponseException(statusLine, response.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}

				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

					/*
					 * String cType = entity.getContentType(); String cSet =
					 * entity.getContentEncoding();
					 * 
					 * ContentType cEntity = ContentType.create(cType,cSet);
					 * 
					 * if (!cEntity.equals(ContentType.TEXT_XML)) { throw new
					 * ClientProtocolException("Unexpected content type:" +
					 * entity.getContentType()); }
					 */
					return docBuilder.parse(entity.getContent());
				} catch (ParserConfigurationException ex) {
					ex.printStackTrace();
					throw new IllegalStateException(ex);
				} catch (SAXException ex) {
					ex.printStackTrace();
					throw new ClientProtocolException("Malformed XML document", ex);
				}
			}
		}
	}
	
	class JsonHandlerClass<T> implements HttpClientResponseHandler<List> {
		@Override
		public List<YarnApp> handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
			{
				ArrayList<YarnApp> resultList = new ArrayList<YarnApp>();
				int statusLine = response.getCode();
				HttpEntity entity = response.getEntity();
				if (statusLine >= 300) {
					throw new HttpResponseException(statusLine, response.getReasonPhrase());
				}
				if (entity == null) {
					throw new ClientProtocolException("Response contains no content");
				}

				try {
					JSONParser parser = new JSONParser();
					Object jsonObj = parser.parse(new InputStreamReader(entity.getContent()));
					
					JSONObject jsonObject = (JSONObject) jsonObj;
					JSONObject appObject = (JSONObject)jsonObject.get("apps");
					JSONArray apps = (JSONArray)appObject.get("app");
										
					Iterator<JSONObject> it = apps.iterator();
					while (it.hasNext()) {
						YarnApp app = new YarnApp();
						JSONObject a = it.next();
					
						app.setName((String)a.get("name"));
						app.setState((String)a.get("state"));
						app.setApplicationTags((String)a.get("applicationTags"));
						app.setApplicationType((String)a.get("ApplicationTypes"));
						app.setStartTime((Long)a.get("startedTime"));
						app.setEndTime((Long)a.get("finishedTime"));
						app.setAmHostHttpAddress((String)a.get("amHostHttpAddress"));
						app.setId((String)a.get("id"));
						app.setUser((String)a.get("user"));
						app.setAmRPCAddress((String)a.get("amRPCAddress"));
												
						resultList.add(app);
					}
					return resultList;
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
					throw new IllegalStateException(e);
				} catch (ParseException e) {
					
					e.printStackTrace();
					throw new ClientProtocolException("Malformed Json document", e);
				}
			}
		}
	}

	private List<String> rmPrivateNames = null;
	private EmrUtils emr = new EmrUtils();
	private static HashMap<String, String> yarnConfigMap = null;

	public YarnUtils() {
		this.rmPrivateNames = emr.getMastersPrivateNames();

	}

	protected String getRMProxy() {
		HashMap<String, String> yarnConfig = this.getRMconfig();
		return yarnConfig.get("yarn.web-proxy.address");
	}

	protected HashMap<String, String> getRMconfig() {
		if (YarnUtils.yarnConfigMap == null) {
			Document result = null;
			try {
				result = Request.get("http://" + rmPrivateNames.get(0) + ":8088/conf").execute()
						.handleResponse(new XmlHandlerClass<Document>());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			result.getDocumentElement().normalize();
			NodeList nList = result.getElementsByTagName("property");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) nNode;
					Node node1 = elem.getElementsByTagName("source").item(0);
					String src = node1.getTextContent();

					if (src.equals("yarn-site.xml") || src.equals("yarn-default.xml")) {
						Node node2 = elem.getElementsByTagName("name").item(0);
						String name = node2.getTextContent();
						Node node3 = elem.getElementsByTagName("value").item(0);
						String value = node3.getTextContent();
						if (YarnUtils.yarnConfigMap == null) {
							YarnUtils.yarnConfigMap = new HashMap<String, String>();
						}
						YarnUtils.yarnConfigMap.put(name, value);
						logger.info(src + " : " + name + " : " + value);
					}
				}
			}
		}
		return YarnUtils.yarnConfigMap;
	}
	
	public List<YarnApp> queryApplications() {
		
		List<YarnApp> result = null;
		try {
			result = Request.get("http://" + rmPrivateNames.get(0) + ":8088/ws/v1/cluster/apps").execute()
					.handleResponse(new JsonHandlerClass<List<YarnApp>>());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	return result;
	}
	
}
