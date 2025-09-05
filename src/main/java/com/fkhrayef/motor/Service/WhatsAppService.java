package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    public WhatsAppService() {
        Unirest.config().reset()      // clear previous (devtools restarts)
                .socketTimeout(10_000)   // 10 sec max wait for response
                .connectTimeout(5_000);  // 5 sec max wait for connection
    }

    public void sendWhatsAppMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ApiException("Message must not be blank");
        }
        try {
            HttpResponse<String> res = Unirest.post("https://api.ultramsg.com/instance141844/messages/chat")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("token", "WHATSAPP_API_KEY")
                    .field("to", "966556663948")
                    .field("body", message)
                    .asString();

        // check HTTP status
        if (res.getStatus() < 200 || res.getStatus() >= 300) {
            String body = res.getBody();
            String snippet = body == null ? "" : body.substring(0, Math.min(body.length(), 256));
            throw new ApiException("Ultramsg error: status=" + res.getStatus() + " body=" + snippet);
        }

        System.out.println("✅ WhatsApp message sent: " + res.getStatus());
        } catch (Exception e) {
            throw new ApiException("Failed to send WhatsApp message: " + e.getMessage());
        }
    }
}
