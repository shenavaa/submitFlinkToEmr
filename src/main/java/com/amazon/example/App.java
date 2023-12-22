package com.amazon.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App 
{
    public static void main( String[] args )
    {
    	
    	Logger logger = LoggerFactory.getLogger(App.class);
        
        EmrUtils emrUtils = new EmrUtils();
        YarnUtils yarn = new YarnUtils();
        
        
        System.out.println(yarn.getRMProxy());
    }
}
