package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.CarDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // TODO: add endpoint to retrieve cars by user id!

    public void addCar(Integer userId, CarDTO carDTO) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Car car = new Car();
        car.setMake(carDTO.getMake());
        car.setModel(carDTO.getModel());
        car.setYear(carDTO.getYear());
        car.setNickname(carDTO.getNickname());
        car.setMileage(carDTO.getMileage());
        car.setVin(carDTO.getVin());
        car.setPurchaseDate(carDTO.getPurchaseDate());
        car.setUser(user);

        carRepository.save(car);
    }

    // TODO: add endpoints to enter registration and insurance (Faisal)

    public void updateCar(Integer id, CarDTO carDTO) {
        Car car = carRepository.findCarById(id);
        if (car == null) {
            throw new ApiException("Car not found");
        }

        car.setMake(carDTO.getMake());
        car.setModel(carDTO.getModel());
        car.setYear(carDTO.getYear());
        car.setNickname(carDTO.getNickname());
        car.setMileage(carDTO.getMileage());
        car.setVin(carDTO.getVin());
        car.setPurchaseDate(carDTO.getPurchaseDate());

        carRepository.save(car);
    }

    public void deleteCar(Integer id) {
        Car car = carRepository.findCarById(id);
        if (car == null) {
            throw new ApiException("Car not found");
        }

        carRepository.delete(car);
    }
}
