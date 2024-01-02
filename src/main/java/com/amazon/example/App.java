package com.amazon.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.example.models.YarnApp;


public class App 
{
    public static void main( String[] args )
    {
    	
    	Logger logger = LoggerFactory.getLogger(App.class);
        
        EmrUtils emrUtils = new EmrUtils();
        YarnUtils yarn = new YarnUtils();
        
        
        for (YarnApp app : yarn.queryApplications()) {
        	System.out.println(app.getId() + " : " + app.getName() + " : " + app.getState() + " : " + app.getTrackingUrl());
        }
    }
}
