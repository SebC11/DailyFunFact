$(document).ready(function() {
    console.log("Document Loaded");
    AWS.config.region = 'us-east-2'; // Region
    AWS.config.credentials = new AWS.CognitoIdentityCredentials({
        IdentityPoolId: 'us-east-2:781da989-a4c3-4dd4-a716-da5e7090be7f',
    });
    var docClient = new AWS.DynamoDB.DocumentClient();
    $('#subscribe').click(function(e) {
        e.preventDefault();
        console.log("Subscribing");
        //Create item in Dynamo
        let emailVal = $('#email').val();
        let params = {
            TableName: 'fun_fact',
            Item: { 'email': emailVal }
        };
        docClient.put(params, function(err, data) {
            if (err) {
                alert("Failed to subscribe your email.");
                console.log(err);
            } else {
                alert("Successfully subscribed for daily fun fact emails")
            }
        });


    });

    $('#unsubscribe').click(function(e) {
        e.preventDefault();
        console.log("Unubscribing");
        //Create item in Dynamo
        let emailVal = $('#email').val();
        let params = {
            TableName: 'fun_fact',
            Key: { 'email': emailVal }
        };
        docClient.delete(params, function(err, data) {
            if (err) {
                alert("Failed to unsubscribe your email");
                console.log(err);
            } else {
                alert("Successfully unsubscribed from daily fun fact emails. We will miss you!")
            }
        });
    });
});