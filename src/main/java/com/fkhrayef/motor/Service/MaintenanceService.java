package com.fkhrayef.motor.Service;


import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.MaintenanceDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Maintenance;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final CarRepository carRepository;

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
        maintenance.setInvoiceFileUrl(maintenanceDTO.getInvoiceFileUrl());
        maintenance.setInvoiceAmount(maintenanceDTO.getInvoiceAmount());
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
        maintenance.setInvoiceFileUrl(maintenanceDTO.getInvoiceFileUrl());
        maintenance.setInvoiceAmount(maintenanceDTO.getInvoiceAmount());

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

}
