package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.DTOin.PaymentRequest;
import com.fkhrayef.motor.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/card")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest paymentRequest){
        return ResponseEntity.status(200).body(paymentService.processPayment(paymentRequest));
    }
    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id){
        return ResponseEntity.status(200).body(paymentService.getPaymentStatus(id));
    }
    @PutMapping("/cancel/{userId}/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(@PathVariable Integer userId, @PathVariable Integer subscriptionId){
        return ResponseEntity.status(200).body(paymentService.cancelSubscription(userId, subscriptionId));
    }
    @DeleteMapping("/delete-card/{paymentId}")
    public ResponseEntity<?> deleteCard(@PathVariable String paymentId) {
        return ResponseEntity.status(200).body(paymentService.deleteCard(paymentId));
    }

}
