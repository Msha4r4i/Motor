package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.DTOin.CarTransferResponseDTO;
import com.fkhrayef.motor.Service.CarTransferRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfer-requests")
@RequiredArgsConstructor
public class CarTransferRequestController {

    private final CarTransferRequestService transferService;


    @PostMapping("/{id}/accept/{actingUserId}")
    public ResponseEntity<?> accept(@PathVariable Integer id , @PathVariable Integer actingUserId){
        return ResponseEntity.status(200).body(transferService.accept(id, actingUserId));
    }

    @PostMapping("/{id}/reject/{actingUserId}")
    public ResponseEntity<?> reject(@PathVariable Integer id, @PathVariable Integer actingUserId) {
        return ResponseEntity.status(200).body(transferService.reject(id, actingUserId));
    }

    @PostMapping("/{id}/cancel/{actingUserId}")
    public ResponseEntity<?> cancel(@PathVariable Integer id, @PathVariable Integer actingUserId) {
        return ResponseEntity.status(200).body(transferService.cancel(id, actingUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(transferService.getOne(id));
    }

    @GetMapping("/incoming/{userId}")
    public ResponseEntity<?> incoming(@PathVariable Integer userId) {
        List<CarTransferResponseDTO> list = transferService.getIncoming(userId);
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/outgoing/{userId}")
    public ResponseEntity<?> outgoing(@PathVariable Integer userId) {
        List<CarTransferResponseDTO> list = transferService.getOutgoing(userId);
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/by-car/{carId}")
    public ResponseEntity<?> byCar(@PathVariable Integer carId) {
        List<CarTransferResponseDTO> list = transferService.getByCar(carId);
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<?> byStatus(@PathVariable String status) {
        List<CarTransferResponseDTO> list = transferService.getByStatus(status);
        return ResponseEntity.status(200).body(list);
    }
    @PostMapping("/direct/{carId}/{fromUserId}/{toEmail}/{toPhone}")
    public ResponseEntity<?> directTransfer(@PathVariable Integer carId, @PathVariable Integer fromUserId, @PathVariable String toEmail, @PathVariable String toPhone) {
        return ResponseEntity.status(200).body(transferService.directTransfer(carId, fromUserId, toEmail, toPhone));
    }

}
