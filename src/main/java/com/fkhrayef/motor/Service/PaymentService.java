package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.PaymentRequest;
import com.fkhrayef.motor.Model.Subscription;
import com.fkhrayef.motor.Repository.PaymentRepository;
import com.fkhrayef.motor.Repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${MOYASAR_API_KEY}")
    private String apiKey;

    private static final String MOYSAR_API_URL = "https://api.moyasar.com/v1/payments/";

    public ResponseEntity<?> processPayment(PaymentRequest paymentRequest){
        String url = "https://api.moyasar.com/v1/payments";

        String callbackUrl ="http://localhost:8080/api/v1/payment/process-payment";

        String requestBody = String.format(
                "source[type]=card" +
                        "&source[name]=%s" +
                        "&source[number]=%s" +
                        "&source[cvc]=%s" +
                        "&source[month]=%s" +
                        "&source[year]=%s" +
                        "&amount=%d" +
                        "&currency=%s" +
                        "&callback_url=%s",
                paymentRequest.getName(),
                paymentRequest.getNumber(),
                paymentRequest.getCvc(),
                paymentRequest.getMonth(),
                paymentRequest.getYear(),
                (int) (paymentRequest.getAmount() * 100),
                paymentRequest.getCurrency(),
                callbackUrl
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey,"");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody,headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,entity,String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public String getPaymentStatus(String paymentId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey,"");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYSAR_API_URL +paymentId,HttpMethod.GET,entity,String.class);
        return response.getBody();
    }

    public ResponseEntity<?> cancelSubscription(Integer userId, Integer subscriptionId) {
        Subscription subscription = subscriptionRepository.findSubscriptionById(subscriptionId);
        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }

        if (!subscription.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Subscription does not belong to this user.");
        }

        if (Boolean.FALSE.equals(subscription.getIsActive())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Subscription already cancelled.");
        }

        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);

        return ResponseEntity.ok("Subscription cancelled successfully.");
    }

    public ResponseEntity<?> deleteCard(String cardId) {
        String deleteUrl = "https://api.moyasar.com/v1/cards/" + cardId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
