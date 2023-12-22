package com.amazon.example;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.HashMap;

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
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class YarnUtils {
	private Logger logger = LoggerFactory.getLogger(YarnUtils.class);

	class myHandlerClass<T> implements HttpClientResponseHandler<Document> {
		@Override
		public Document handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
			{
				int statusLine = response.getCode();
				HttpEntity entity =  response.getEntity();
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
					String cType = entity.getContentType();
					String cSet = entity.getContentEncoding();
					
					ContentType cEntity =  ContentType.create(cType,cSet);
					
					if (!cEntity.equals(ContentType.TEXT_XML)) {
						throw new ClientProtocolException("Unexpected content type:" + entity.getContentType());
					}
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

	private List<String> rmPrivateNames = null;
	private EmrUtils emr = new EmrUtils();
	private static HashMap<String,String> yarnConfigMap = null;

	public YarnUtils() {
		this.rmPrivateNames = emr.getMastersPrivateNames();

	}
	
	protected String getRMProxy() {
		HashMap<String,String> yarnConfig = this.getRMconfig();
        	return yarnConfig.get("yarn.web-proxy.address");
	}

	protected HashMap<String,String> getRMconfig() {
		if (YarnUtils.yarnConfigMap == null){
			Document result = null;
				try {
					result = Request.get("http://" + rmPrivateNames.get(0) + ":8088/conf").execute()
						.handleResponse(new myHandlerClass<Document>());
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

                                	if (src.equals("yarn-site.xml") || src.equals("yarn-default.xml")){
                                        	Node node2 = elem.getElementsByTagName("name").item(0);
                                        	String name = node2.getTextContent();
                                        	Node node3 = elem.getElementsByTagName("value").item(0);
                                        	String value = node3.getTextContent();
						if (YarnUtils.yarnConfigMap == null) {
							YarnUtils.yarnConfigMap = new HashMap<String,String>();
						}
						YarnUtils.yarnConfigMap.put(name,value);
                                        	logger.info(src + " : " + name + " : " + value );
                                	}
                        	}
                	}
		}
		return YarnUtils.yarnConfigMap;
	}
}
