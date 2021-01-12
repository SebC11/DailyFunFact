# DailyFunFact

Serverless Web App which allows users to subscribe to daily fun fact emails.  
### [Link to Site](https://main.d2rwu1vabov5ga.amplifyapp.com/?) ###  
![plot](./images/websiteSC.png)

## Lambda ##

This project utilizes a java lambda function triggered by a cloudwatch scheduled event in order to automate the daily sending of the emails to subscribers. The lambda utilizes a config.properties file in order to hide confidential data as well as allow the program to be usable by others. The lambda pulls the emails from the DynamoDB Table and then calls a [Fun Fact API](https://uselessfacts.jsph.pl/ "Fun Fact API") to fill the content of the email. Using JavaXMail and a gmail account, I am able to use google's smtp server in order to send the daily facts to the subscribers. I use TLS for the trasnport and MimeMessage to build the email componenets together. Maven is used for the dependency management.

Sample email:  

![plot](./images/sampleEmail.png)

## DynamoDB ##

This project uses a fairly simple table within DynamoDB called fun_fact. It is used to store all the subscribers to the emails and is conveniently pulled through a DynamoDB Scan request in the lambda function. The primary key is the emails.

## Website ##

The website is built using bootstrap and jquery. It hosts a simple static frontend with an email form and then a subscribe and unsubscribe button. I use jquery and ajax to handle the click events. Additionally, I utilize the AWS JS SDK in order to make my calls to Amazon Cognito for the credentials in the identity pool as well as finally to either put or delete a user's email from the DB. The website is deployed using a custom build path as part of AWS Amplify and is hosted there.


