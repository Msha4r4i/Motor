package com.fkhrayef.motor.Repository;

import com.fkhrayef.motor.Model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    Maintenance findMaintenanceById(Integer id);
}
