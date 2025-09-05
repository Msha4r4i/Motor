package com.fkhrayef.motor.Controller;

import com.fkhrayef.motor.DTOout.*;
import com.fkhrayef.motor.Service.CarAIService;
import com.fkhrayef.motor.Service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/car-ai")
public class CarAIController {

    @Autowired
    private CarAIService carAIService;

    @Autowired
    private RAGService ragService;

    @PostMapping("/upload-manual/{carId}")
    public ResponseEntity<?> uploadManual(
            @PathVariable Integer carId,
            @RequestParam("file") MultipartFile file) {

        ManualUploadResponse response = carAIService.uploadManual(carId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ask/{carId}")
    public ResponseEntity<?> askQuestion(
            @PathVariable Integer carId,
            @RequestParam String question) {

        QuestionResponse response = carAIService.askQuestion(carId, question);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/car/{carId}/info")
    public ResponseEntity<?> getCarDocumentInfo(@PathVariable Integer carId) {
        CarDocumentInfoResponse info = carAIService.getCarDocumentInfo(carId);
        return ResponseEntity.status(HttpStatus.OK).body(info);
    }

    // Admin endpoints
    @GetMapping("/admin/documents")
    public ResponseEntity<?> getAllDocuments() {
        DocumentsInfoResponse documents = ragService.getDocumentsInfo();
        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }

    @GetMapping("/admin/search")
    public ResponseEntity<?> searchDocuments(@RequestParam(required = false) String query) {
        DocumentsInfoResponse documents = ragService.getDocumentsInfo();
        List<String> documentNames = documents.getDocument_names();
        
        if (query == null || query.trim().isEmpty()) {
            AllDocumentsResponse response = new AllDocumentsResponse();
            response.setDocuments(documentNames);
            response.setTotal_count(documents.getTotal_documents());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        
        List<String> filteredDocs = documentNames.stream()
                .filter(doc -> doc.toLowerCase().contains(query.toLowerCase()))
                .toList();
        
        DocumentSearchResponse response = new DocumentSearchResponse();
        response.setQuery(query);
        response.setDocuments(filteredDocs);
        response.setCount(filteredDocs.size());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/admin/check-document-exists")
    public ResponseEntity<?> checkDocumentExists(@RequestParam String documentName) {
        boolean exists = ragService.documentExists(documentName);
        DocumentExistsResponse response = new DocumentExistsResponse();
        response.setDocument_name(documentName);
        response.setExists(exists);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
