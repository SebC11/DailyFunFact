package com.amazonaws.lambda.demo;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public class LambdaFunctionHandler {
	LambdaLogger log;
	
    public void handleRequest(ScheduledEvent event, Context context) {
    	log = context.getLogger();
        //TODO: Fetch Emails from Dynamo and throw them into a list
        
        //TODO: Send get request to Fun Fact API to get Message for the email
    	//Fetching Properties file
        Properties prop = new Properties();
        FileInputStream ip;
        try {
        	ip = new FileInputStream("/FunFactLambdas/src/main/resources/config.properties");
        	prop.load(ip);
        	log.log("Succesfully pulled Properties");
        } catch (Exception e) {
        	log.log("Failed to pull from config.properties");
        	System.exit(1);
        }
        //loading in properties
        String from = prop.getProperty("from");
        String pw = prop.getProperty("password");
        String subject = prop.getProperty("subject");
        String smtpUrl = prop.getProperty("smtpUrl");
        String smtpPort = prop.getProperty("smtpPort");
        //Temp usage. Should probably upgrade to Cognito
        String accessKeyId = prop.getProperty("accessKeyId");
        String secretAccessKey = prop.getProperty("secretAccessKey");
        //making sure config file is set up properly
        if(from == null || pw == null || subject == null || smtpUrl == null || smtpPort == null) {
        	log.log("Incorrect config file setup");
        	System.exit(1);
        }
        //need to build the props
        Address [] addresses = null;
        Session session = Session.getDefaultInstance(prop);
        MimeMessage msg = new MimeMessage(session);
        try {
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.BCC, addresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
		} catch (Exception e) {
			log.log("Error making message");
			System.exit(1);
		}
        //TODO: Send the Email
        
    }
    
    private void sendEmail(String message, List<String> to)
    {
    	
    }
}
