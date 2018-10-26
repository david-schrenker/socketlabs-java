package examples.basic.invalid;

import com.socketLabs.injectionApi.*;
import com.socketLabs.injectionApi.message.*;
import examples.*;

public class BasicSendWithInvalidFrom implements Example {

    public SendResponse RunExample ()  throws Exception {

        BasicMessage message = new BasicMessage();

        message.setSubject("Sending A Test Message");
        message.setHtmlBody("<html>This is the Html Body of my message.</html>");
        message.setPlainTextBody("This is the Plain Text Body of my message.");

        message.setFrom(new EmailAddress("!@#$!@#$!@#$@#!$"));
        message.addToEmailAddress("recipient@example.com");

        // create the client
        SocketLabsClient client = new SocketLabsClient(ExampleConfig.ServerId, ExampleConfig.ApiKey);

        // send the message
        SendResponse response =  client.send(message);

        return response;

    }
}