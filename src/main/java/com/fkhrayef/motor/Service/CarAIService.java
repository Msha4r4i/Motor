package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOout.*;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CarAIService {

    @Autowired
    private RAGService ragService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private CarRepository carRepository;

    public ManualUploadResponse uploadManual(Integer carId, MultipartFile file) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ApiException("Only PDF files are allowed");
        }

        // Generate catalog path: YYYY-make-model-owner-manual.pdf
        String catalogPath = String.format("%d-%s-%s-owner-manual.pdf",
                car.getYear(),
                car.getMake().toLowerCase().replace(" ", "-"),
                car.getModel().toLowerCase().replace(" ", "-"));

        // Check if manual already exists in S3
        if (s3Service.catalogFileExists(catalogPath)) {
            throw new ApiException("Manual for this car already exists: " + catalogPath);
        }

        // Check if document already exists in RAG system
        String documentName = generateDocumentName(car);

        if (ragService.documentExists(documentName)) {
            throw new ApiException("Manual for this car already exists in the system: " + documentName);
        }

        // Upload to S3
        String s3Url;
        try {
            s3Url = s3Service.uploadCatalogFile(file, catalogPath);
        } catch (Exception e) {
            throw new ApiException("Failed to upload file: " + e.getMessage());
        }

        // Process document in RAG system
        boolean success = ragService.processDocument(s3Url, documentName);
        if (!success) {
            throw new ApiException("Failed to process document in RAG system");
        }

        ManualUploadResponse response = new ManualUploadResponse();
        response.setStatus("success");
        response.setMessage("Manual uploaded and processed successfully");
        response.setDocumentName(documentName);
        response.setS3Url(s3Url);
        response.setCarId(carId.toString());
        return response;
    }

    public QuestionResponse askQuestion(Integer carId, String question) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        // Generate document name from car details
        String documentName = generateDocumentName(car);

        // Check if document exists in RAG system
        if (!ragService.documentExists(documentName)) {
            throw new ApiException("Manual for this car is not available. Please upload the manual first.");
        }

        QuestionResponse response = ragService.askQuestion(question, documentName);
        return response;
    }

    public CarDocumentInfoResponse getCarDocumentInfo(Integer carId) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        // Generate document name from car details
        String documentName = generateDocumentName(car);

        boolean hasManual = ragService.documentExists(documentName);

        CarDocumentInfoResponse response = new CarDocumentInfoResponse();
        response.setCarId(carId.toString());
        response.setCarInfo(String.format("%d %s %s", car.getYear(), car.getMake(), car.getModel()));
        response.setDocumentName(documentName);
        response.setHasManual(hasManual ? "true" : "false");
        return response;
    }

    // Helper method to avoid code duplication
    private String generateDocumentName(Car car) {
        return String.format("%d %s %s owner-manual",
                car.getYear(),
                car.getMake(),
                car.getModel());
    }
}
