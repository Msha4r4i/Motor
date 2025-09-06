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
import java.util.Map;
import java.util.Set;

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
        validateCarMakeAndModel(carDTO);

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

        validateCarMakeAndModel(carDTO);

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

    public List<Car> getCarsByUserId(Integer userId){
        User user = userRepository.findUserById(userId);

        if (user == null){
            throw new ApiException("User not found");
        }

        return carRepository.findCarsByUserId(user.getId());
    }

    private static final Map<String, Set<String>> MAKE_MODELS = Map.ofEntries(
            Map.entry("Toyota", Set.of("Land Cruiser","Prado","Camry","Corolla","Hilux","Yaris","Avalon")),
            Map.entry("Lexus", Set.of("LX570","ES350")),
            Map.entry("Hyundai", Set.of("Sonata","Elantra","Tucson","Palisade")),
            Map.entry("Nissan", Set.of("Altima","Patrol","X-Trail","Sentra")),
            Map.entry("Kia", Set.of("Sportage","Sorento")),
            Map.entry("Chevrolet", Set.of("Tahoe","Suburban","Silverado")),
            Map.entry("GMC", Set.of("Yukon","Sierra")),
            Map.entry("Ford", Set.of("F-150","Explorer")),
            Map.entry("Mazda", Set.of("CX-5")),
            Map.entry("Mitsubishi", Set.of("L200")),
            Map.entry("Isuzu", Set.of("D-Max"))
    );


    public void validateCarMakeAndModel(CarDTO carDTO) {
        String make = carDTO.getMake();
        String model = carDTO.getModel();

        if (!MAKE_MODELS.containsKey(make)) {
            throw new ApiException("Unsupported make: " + make);
        }
        if (!MAKE_MODELS.get(make).contains(model)) {
            throw new ApiException("Model " + model + " does not belong to " + make);
        }
    }
}
