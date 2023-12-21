package com.amazon.example;

import java.io.IOException;
import java.util.List;

import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;

public class YarnUtils {
	private List<String> rmPrivateNames=null;
	private EmrUtils emr = new EmrUtils();
	
	public YarnUtils() {
		this.rmPrivateNames=emr.getMastersPrivateNames();
		
		
	}
		
	
	protected String getRMconfig() {
		Content result =null;
		try {
			 result = Request.get("http://" + rmPrivateNames.get(0) + ":8088/conf").execute().returnContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.asString();
	
	}
}
