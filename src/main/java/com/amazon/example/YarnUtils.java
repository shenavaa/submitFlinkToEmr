package com.amazon.example;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.w3c.dom.Document;

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

public class YarnUtils {

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

	public YarnUtils() {
		this.rmPrivateNames = emr.getMastersPrivateNames();

	}

	protected String getRMconfig() {
		Document result = null;
		try {
			result = Request.get("http://" + rmPrivateNames.get(0) + ":8088/conf").execute()
					.handleResponse(new myHandlerClass<Document>());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.getTextContent();

	}
}
