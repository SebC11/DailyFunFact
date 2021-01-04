package com.amazonaws.lambda.demo;

import com.amazonaws.services.lambda.runtime.Context;

import com.amazonaws.services.lambda.runtime.events.*;; 

public class LambdaFunctionHandler {

    public void handleRequest(ScheduledEvent event, Context context) {
        context.getLogger().log("Test");
    }
}
