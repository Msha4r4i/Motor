package com.fkhrayef.motor.Service;


import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.MaintenanceDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Maintenance;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final CarRepository carRepository;
    private final S3Service s3Service;

    public List<Maintenance> getAllMaintenances(){
        return maintenanceRepository.findAll();
    }

    public void addMaintenance(Integer carId, MaintenanceDTO maintenanceDTO){
        Car car = carRepository.findCarById(carId);
        if (car == null){
            throw new ApiException("Car not found !");
        }

        Maintenance maintenance = new Maintenance();

        maintenance.setRecordType(maintenanceDTO.getRecordType());
        maintenance.setServiceType(maintenanceDTO.getServiceType());
        maintenance.setServiceDate(maintenanceDTO.getServiceDate());
        maintenance.setMileage(maintenanceDTO.getMileage());
        maintenance.setNotes(maintenanceDTO.getNotes());
        maintenance.setCar(car);

        maintenanceRepository.save(maintenance);

    }

    public void updateMaintenance(Integer id, MaintenanceDTO maintenanceDTO){
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(id);
        if (maintenance == null){
            throw new ApiException("Maintenance not found !");
        }
        maintenance.setRecordType(maintenanceDTO.getRecordType());
        maintenance.setServiceType(maintenanceDTO.getServiceType());
        maintenance.setServiceDate(maintenanceDTO.getServiceDate());
        maintenance.setMileage(maintenanceDTO.getMileage());
        maintenance.setNotes(maintenanceDTO.getNotes());

        maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Integer id){
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(id);
        if (maintenance == null){
            throw new ApiException("Maintenance not found !");
        }
        maintenanceRepository.delete(maintenance);
    }

    public List<Maintenance> getMaintenancesByCarId(Integer carId){
        Car car = carRepository.findCarById(carId);

        if (car == null){
            throw new ApiException("Car not found");
        }
        return maintenanceRepository.findMaintenancesByCarId(car.getId());
    }

    public Maintenance getMaintenanceById(Integer id) {
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(id);
        if (maintenance == null) {
            throw new ApiException("Maintenance not found with id: " + id);
        }
        return maintenance;
    }

    // Invoice file management
    public void uploadInvoice(Integer maintenanceId, MultipartFile file, Double invoiceAmount) {
        // Get maintenance details from database
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(maintenanceId);
        if (maintenance == null) {
            throw new ApiException("Maintenance not found with id: " + maintenanceId);
        }

        // Validate file presence
        if (file == null || file.isEmpty()) {
            throw new ApiException("Invoice file is required");
        }
        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ApiException("Only PDF files are allowed for invoice upload");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("Invoice file size cannot exceed 10MB");
        }

        // Validate invoice amount
        if (invoiceAmount == null || invoiceAmount <= 0) {
            throw new ApiException("Invoice amount must be greater than 0");
        }

        // Upload to S3 with unique naming
        String s3Url;
        try {
            Car car = maintenance.getCar();
            s3Url = s3Service.uploadMaintenanceInvoiceFile(file, maintenanceId.toString(), car.getMake(), car.getModel());
        } catch (Exception e) {
            throw new ApiException("Failed to upload invoice file: " + e.getMessage());
        }

        // Update maintenance record with invoice information
        maintenance.setInvoiceFileUrl(s3Url);
        maintenance.setInvoiceAmount(invoiceAmount);
        maintenanceRepository.save(maintenance);
    }

    public byte[] downloadInvoice(Integer maintenanceId) {
        // Get maintenance details from database
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(maintenanceId);
        if (maintenance == null) {
            throw new ApiException("Maintenance not found with id: " + maintenanceId);
        }

        if (maintenance.getInvoiceFileUrl() == null) {
            throw new ApiException("No invoice file found for this maintenance record");
        }

        // Extract the S3 key from the URL
        String s3Url = maintenance.getInvoiceFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/maintenance-invoices/") + 1); // Extract "maintenance-invoices/maintenance-123-toyota-camry-invoice.pdf"

        // Download file from S3
        return s3Service.downloadFile(key);
    }

    public void deleteInvoice(Integer maintenanceId) {
        // Get maintenance details from database
        Maintenance maintenance = maintenanceRepository.findMaintenanceById(maintenanceId);
        if (maintenance == null) {
            throw new ApiException("Maintenance not found with id: " + maintenanceId);
        }

        if (maintenance.getInvoiceFileUrl() == null) {
            throw new ApiException("No invoice file found for this maintenance record");
        }

        // Extract the S3 key from the URL
        String s3Url = maintenance.getInvoiceFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/maintenance-invoices/") + 1); // Extract "maintenance-invoices/maintenance-123-toyota-camry-invoice.pdf"

        // Delete file from S3
        try {
            s3Service.deleteFile(key);
        } catch (Exception e) {
            throw new ApiException("Failed to delete invoice file from S3: " + e.getMessage());
        }

        // Clear invoice information from maintenance record
        maintenance.setInvoiceFileUrl(null);
        maintenanceRepository.save(maintenance);
    }

}
