package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.QuestionRequest;
import com.fkhrayef.motor.DTOout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class RAGService {

    private final WebClient ragApiClient;

    @Autowired
    public RAGService(WebClient ragApiClient) {
        this.ragApiClient = ragApiClient;
    }

    public QuestionResponse askQuestion(String question, String documentName) {
        try {
            QuestionRequest request = new QuestionRequest(question, documentName);

            QuestionResponse response = ragApiClient
                    .post()
                    .uri("/ask")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("HTTP " + clientResponse.statusCode() + ": " + body)))
                    .bodyToMono(QuestionResponse.class)
                    .block();

            if (response != null) {
                // Set API response fields
                response.setDocument_name(documentName);
                
                // Extract document name from metadata if available
                if (response.getMetadata() != null && response.getMetadata().containsKey("document_name")) {
                    response.setDocument_name(response.getMetadata().get("document_name").toString());
                }
                
                // Extract source pages for user-friendly display
                response.extractSourcePages();
            }

            return response != null ? response : new QuestionResponse();

        } catch (Exception e) {
            throw new ApiException("Failed to get answer: " + e.getMessage());
        }
    }

    public boolean processDocument(String s3Url, String documentName) {
        try {
            Map<String, String> request = Map.of(
                "s3_url", s3Url,
                "document_name", documentName
            );

            String response = ragApiClient
                    .post()
                    .uri("/process-s3")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response != null && response.contains("success");

        } catch (Exception e) {
            throw new ApiException("Failed to process document: " + e.getMessage());
        }
    }

    public DocumentsInfoResponse getDocumentsInfo() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = ragApiClient
                    .get()
                    .uri("/documents/info")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            DocumentsInfoResponse dto = new DocumentsInfoResponse();
            dto.setTotal_documents((Integer) response.get("total_documents"));
            @SuppressWarnings("unchecked")
            List<String> documentNames = (List<String>) response.get("document_names");
            dto.setDocument_names(documentNames);
            @SuppressWarnings("unchecked")
            Map<String, Integer> documentsCount = (Map<String, Integer>) response.get("documents_count");
            dto.setDocuments_count(documentsCount);
            dto.setVectorstores_ready((Integer) response.get("vectorstores_ready"));
            dto.setRag_chains_ready((Integer) response.get("rag_chains_ready"));
            dto.setS3_enabled((Boolean) response.get("s3_enabled"));
            
            return dto;
        } catch (Exception e) {
            throw new ApiException("Failed to get documents info: " + e.getMessage());
        }
    }

    public boolean documentExists(String documentName) {
        try {
            DocumentsInfoResponse documents = getDocumentsInfo();
            
            // Check if document exists in the document_names array
            if (documents.getDocument_names() != null) {
                return documents.getDocument_names().contains(documentName);
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
