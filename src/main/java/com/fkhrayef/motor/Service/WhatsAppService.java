package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    public WhatsAppService() {
        Unirest.config().reset()      // clear previous (devtools restarts)
                .socketTimeout(0)
                .connectTimeout(0);
    }

    public void sendWhatsAppMessage(String message) {
        try {
            HttpResponse<String> res = Unirest.post("https://api.ultramsg.com/instance141844/messages/chat")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("token", "7j9ikfkacoa2br4v")
                    .field("to", "966556663948")   // or another 9665XXXXXXX number
                    .field("body", message)
                    .asString();

            System.out.println(res.getStatus() + " -> " + res.getBody());
        } catch (Exception e) {
            throw new ApiException("Failed to send WhatsApp message: " + e.getMessage());
        }
    }
}
