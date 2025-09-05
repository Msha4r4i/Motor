package com.fkhrayef.motor.Repository;

import com.fkhrayef.motor.Model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    Car findCarById(Integer id);

    List<Car> findCarsByUserId(Integer id);
}
