package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.CarDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.Subscription;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.ReturnableType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // TODO: add endpoint to retrieve cars by user id!

    public void addCar(Integer userId, CarDTO carDTO) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        // enforce subscription rules
        enforceCarLimit(user);

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

    // Registration file management
    public void uploadRegistration(Integer carId, MultipartFile file, LocalDate registrationExpiry) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        // Validate file presence
        if (file == null || file.isEmpty()) {
            throw new ApiException("Registration file is required");
        }
        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ApiException("Only PDF files are allowed for registration upload");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("Registration file size cannot exceed 10MB");
        }

        // Validate registration expiry date
        if (registrationExpiry.isBefore(LocalDate.now())) {
            throw new ApiException("Registration expiry date must be in the future");
        }

        // Upload to S3 with unique naming
        String s3Url;
        try {
            s3Url = s3Service.uploadRegistrationFile(file, carId.toString(), car.getMake(), car.getModel());
        } catch (Exception e) {
            throw new ApiException("Failed to upload registration file: " + e.getMessage());
        }

        // Update car record with registration information
        car.setRegistrationFileUrl(s3Url);
        car.setRegistrationExpiry(registrationExpiry);
        carRepository.save(car);
    }

    public byte[] downloadRegistration(Integer carId) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        if (car.getRegistrationFileUrl() == null) {
            throw new ApiException("No registration file found for this car");
        }

        // Extract the S3 key from the URL
        String s3Url = car.getRegistrationFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/registrations/") + 1); // Extract "registrations/car-123-toyota-camry-registration.pdf"

        // Download file from S3
        return s3Service.downloadFile(key);
    }

    public void deleteRegistration(Integer carId) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        if (car.getRegistrationFileUrl() == null) {
            throw new ApiException("No registration file found for this car");
        }

        // Extract the S3 key from the URL
        String s3Url = car.getRegistrationFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/registrations/") + 1); // Extract "registrations/car-123-toyota-camry-registration.pdf"

        // Delete file from S3
        try {
            s3Service.deleteFile(key);
        } catch (Exception e) {
            throw new ApiException("Failed to delete registration file from S3: " + e.getMessage());
        }

        // Clear registration information from car record
        car.setRegistrationFileUrl(null);
        car.setRegistrationExpiry(null);
        carRepository.save(car);
    }

    // Insurance file management
    public void uploadInsurance(Integer carId, MultipartFile file, LocalDate insuranceEndDate) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ApiException("Only PDF files are allowed for insurance upload");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("Insurance file size cannot exceed 10MB");
        }

        // Validate insurance end date
        if (insuranceEndDate.isBefore(LocalDate.now())) {
            throw new ApiException("Insurance end date must be in the future");
        }

        // Upload to S3 with unique naming
        String s3Url;
        try {
            s3Url = s3Service.uploadInsuranceFile(file, carId.toString(), car.getMake(), car.getModel());
        } catch (Exception e) {
            throw new ApiException("Failed to upload insurance file: " + e.getMessage());
        }

        // Update car record with insurance information
        car.setInsuranceFileUrl(s3Url);
        car.setInsuranceEndDate(insuranceEndDate);
        carRepository.save(car);
    }

    public byte[] downloadInsurance(Integer carId) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        if (car.getInsuranceFileUrl() == null) {
            throw new ApiException("No insurance file found for this car");
        }

        // Extract the S3 key from the URL
        String s3Url = car.getInsuranceFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/insurances/") + 1); // Extract "insurances/car-123-toyota-camry-insurance.pdf"

        // Download file from S3
        return s3Service.downloadFile(key);
    }

    public void deleteInsurance(Integer carId) {
        // Get car details from database
        Car car = carRepository.findCarById(carId);
        if (car == null) {
            throw new ApiException("Car not found with id: " + carId);
        }

        if (car.getInsuranceFileUrl() == null) {
            throw new ApiException("No insurance file found for this car");
        }

        // Extract the S3 key from the URL
        String s3Url = car.getInsuranceFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/insurances/") + 1); // Extract "insurances/car-123-toyota-camry-insurance.pdf"

        // Delete file from S3
        try {
            s3Service.deleteFile(key);
        } catch (Exception e) {
            throw new ApiException("Failed to delete insurance file from S3: " + e.getMessage());
        }

        // Clear insurance information from car record
        car.setInsuranceFileUrl(null);
        car.setInsuranceEndDate(null);
        carRepository.save(car);
    }

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

    public String getMaintenanceCostOneYear(String make, String model, Integer minMileage, Integer maxMileage) {

        if (!MAKE_MODELS.containsKey(make) || !MAKE_MODELS.get(make).contains(model)) {
            throw new ApiException("Unsupported make/model");
        }

        if (minMileage != null && maxMileage != null && minMileage > maxMileage) {
                    throw new ApiException("minMileage cannot be greater than maxMileage");
        }

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        List<Car> cars = carRepository.findByMakeAndModel(make, model).stream()
                .filter(c -> minMileage == null || (c.getMileage() != null && c.getMileage() >= minMileage))
                .filter(c -> maxMileage == null || (c.getMileage() != null && c.getMileage() <= maxMileage))
                .toList();


        List<Double> costs = new ArrayList<>();
        for (Car c : cars) {
            double spend = c.getMaintenances() == null ? 0.0 :
                    c.getMaintenances().stream()
                            .filter(m -> "MAINTENANCE".equalsIgnoreCase(m.getRecordType()))
                            .filter(m -> m.getServiceDate() != null && !m.getServiceDate().isBefore(oneYearAgo))
                            .mapToDouble(m -> m.getInvoiceAmount() == null ? 0.0 : m.getInvoiceAmount())
                            .sum();
            costs.add(spend);
        }

        if (costs.isEmpty()) return "Maintenance cost in last year: 0 SAR (0 cars)";

        double avg = costs.stream().mapToDouble(d -> d).average().orElse(0.0);
        long count = costs.size();

        return String.format("Maintenance cost in last year: %.2f SAR (based on %d cars)", avg, count);
    }

    public String getVisitFrequency(String make, String model, Integer minAge, Integer maxAge) {
        if (!MAKE_MODELS.containsKey(make) || !MAKE_MODELS.get(make).contains(model)) {
            throw new ApiException("Unsupported make/model");
        }

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        List<Car> cars = carRepository.findAll().stream()
                .filter(c -> make.equals(c.getMake()) && model.equals(c.getModel()))
                .filter(c -> {
                    if (c.getPurchaseDate() == null) return true;
                    int age = today.getYear() - c.getPurchaseDate().getYear();
                    return (minAge == null || age >= minAge) && (maxAge == null || age <= maxAge);
                })
                .toList();

        List<Long> yearlyVisits = new ArrayList<>();
        for (Car c : cars) {
            if (c.getMaintenances() == null) continue;

            long visits = c.getMaintenances().stream()
                    .filter(m -> "MAINTENANCE".equalsIgnoreCase(m.getRecordType()))
                    .filter(m -> m.getServiceDate() != null && !m.getServiceDate().isBefore(oneYearAgo))
                    .count();

            yearlyVisits.add(visits);
        }

        if (yearlyVisits.isEmpty()) return "No data for this car";

        double avg = yearlyVisits.stream().mapToLong(v -> v).average().orElse(0);
        long count = yearlyVisits.size();

        if (avg == 0) {
            return String.format("On average, cars had no maintenance visits in the last year (based on %d cars)", count);
        }

        double yearsBetween = 1 / avg;
        return String.format("On average, cars are serviced once every ~%.0f years (based on %d cars)", yearsBetween, count);
    }

    public String getTypicalMileagePerYear(String make, String model, String city) {
        if (!MAKE_MODELS.containsKey(make) || !MAKE_MODELS.get(make).contains(model)) {
            throw new ApiException("Unsupported make/model");
        }

        LocalDate today = LocalDate.now();

        List<Car> cars = carRepository.findByMakeAndModel(make, model).stream()
                .filter(c -> city == null ||
                        (c.getUser() != null && c.getUser().getCity() != null && city.equalsIgnoreCase(c.getUser().getCity())))
                .toList();

        List<Double> mileagePerYear = new ArrayList<>();
        for (Car c : cars) {
            if (c.getMileage() == null || c.getPurchaseDate() == null) continue;

            long daysOwned = java.time.temporal.ChronoUnit.DAYS.between(c.getPurchaseDate(), today);
            double yearsOwned = Math.max(1.0, daysOwned / 365.25);

            mileagePerYear.add(c.getMileage() / yearsOwned);
        }

        if (mileagePerYear.isEmpty()) return "No mileage data for this car";

        double avg = mileagePerYear.stream().mapToDouble(d -> d).average().orElse(0);
        long count = mileagePerYear.size();

        return String.format("Typical mileage per year: %.0f km (based on %d cars)", avg, count);
    }


    public void updateMileage(Integer userId, Integer carId, Integer newMileage) {
        Car car = carRepository.findCarByIdAndUserId(carId, userId);
        if (car == null) {
            throw new ApiException("Car not found or does not belong to this user");
        }

        if (newMileage == null) {
            throw new ApiException("New mileage is required");
        }

        if (newMileage < car.getMileage()) {
            throw new ApiException("New mileage cannot be less than current mileage (" + car.getMileage() + ")");
        }

        car.setMileage(newMileage);
        carRepository.save(car);
    }

    public Integer getCarsNumbers(Integer userId){
        return carRepository.findCarsByUserId(userId).size();
    }

    public void enforceCarLimit(User user) {
        int existing = getCarsNumbers(user.getId());
        Subscription sub = user.getSubscription();

        if (sub == null) { // Free (no subscription)
            if (existing >= 1) throw new ApiException("Free plan allows only 1 car, you would have to upgrade you subscription type to Pro To add more than 1-5 cars !");
            return;
        }

        String plan = sub.getPlanType() == null ? "" : sub.getPlanType().toLowerCase();
        switch (plan) {
            case "pro":
                if (existing >= 5) throw new ApiException("Pro plan allows up to 5 cars, you would have to upgrade you subscription type to Enterprise To add more than 5 cars !");
                break;
            case "enterprise":
                // unlimited → no check
                break;
            default:
                if (existing >= 1) throw new ApiException("This plan allows only 1 car");
        }
    }


}
