package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LambdaFunctionHandler {
	LambdaLogger logger;

	public void handleRequest(ScheduledEvent event, Context context) {
		logger = context.getLogger();
		// TODO: Fetch Emails from Dynamo and throw them into a list
		
		
		// Fetching Properties file
		Properties prop = new Properties();
		FileInputStream ip;
		
		try {
			ip = new FileInputStream("/FunFactLambdas/src/main/resources/config.properties");
			prop.load(ip);
			logger.log("Succesfully pulled Properties");
		} catch (Exception e) {
			logger.log("Failed to pull from config.properties");
			System.exit(1);
		}
		logger.log("Loading Properties");
		// loading in properties
		try {
			String user = prop.getProperty("user");
			String pw = prop.getProperty("password");
			String subject = prop.getProperty("subject");
			String smtpUrl = prop.getProperty("smtpUrl");
			String smtpPort = prop.getProperty("smtpPort");
			// Temp usage. Should probably upgrade to Cognito
			String accessKeyId = prop.getProperty("accessKeyId");
			String secretAccessKey = prop.getProperty("secretAccessKey");
			// making sure config file is set up properly
			if (user == null || pw == null || subject == null || smtpUrl == null || smtpPort == null) {
				logger.log("Incorrect config file setup");
				System.exit(1);
			}
			
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
					.withRegion("us-east-1")
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.build();

			ScanRequest scanRequest = new ScanRequest()
			    .withTableName("fun_fact");
			List<String> addressList = new ArrayList<String>();
			ScanResult result = client.scan(scanRequest);
			for (Map<String, AttributeValue> item : result.getItems()){
			    addressList.add(item.get("email").toString());
			}
			
			//Now convert to address array
			Address[] addressArray = new Address[addressList.size()];
			for(int i = 0; i < addressList.size(); ++i) {
				try {
					addressArray[i] = new InternetAddress(addressList.get(i));
				} catch (Exception ex) {
					logger.log("Invalid Email");
					logger.log("EXCEPTION:" + ex);
					logger.log("REASON: " + ex.getMessage());
					System.exit(1);
				}
			}
			
			String funFact = fetchFact();
			
			// need to build the props
			Session session = Session.getDefaultInstance(prop);
			MimeMessage msg = new MimeMessage(session);
			try {
				msg.setFrom(new InternetAddress(user));
				msg.setRecipients(Message.RecipientType.BCC, addressArray);
				msg.setSubject(subject);
				msg.setSentDate(new Date());
				msg.setText(funFact);
			} catch (Exception ex) {
				logger.log("Error making message");
				logger.log("EXCEPTION:" + ex);
				logger.log("REASON: " + ex.getMessage());
				System.exit(1);
			}
			
			Transport transport = session.getTransport("smtp");
			try {
				logger.log("Connecting to email server");    				
				transport.connect(prop.getProperty("mail.smtp.host"), Integer.parseInt(prop.getProperty("mail.smtp.port")), user, pw);
				logger.log("Sending email message");
				transport.sendMessage(msg, msg.getAllRecipients());
				logger.log("Email sent");
			} catch (Exception ex) {
				logger.log("EXCEPTION:" + ex);
				logger.log("REASON: " + ex.getMessage());
			}
		} catch (Exception ex) {
			logger.log("Failed to send email");
			logger.log("EXCEPTION:" + ex);
			logger.log("REASON: " + ex.getMessage());
		}

	}
	
	//Sends GET Request to Fun facts API and returns the fun fact from the json response
	private String fetchFact() {
		
		try {
			//Setting up URL for calling api
			URL url = new URL("https://uselessfacts.jsph.pl/today.json?language=en");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			logger.log("Sending 'GET' request to URL : " + url);
			logger.log("Response Code: " + responseCode);
			//Reading Response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(response.toString());
			JSONObject obj = new JSONObject(response);
			return obj.getString("text");
			
		} catch (Exception ex) {
			logger.log("EXCEPTION:" + ex);
			logger.log("REASON: " + ex.getMessage());
			System.exit(1);
		}
		return null;
	}
	
	

}
