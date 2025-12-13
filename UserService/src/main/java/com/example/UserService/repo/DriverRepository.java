package com.example.UserService.repo;

import com.example.UserService.models.DriverModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DriverRepository extends MongoRepository<DriverModel, String> {

    Optional<DriverModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
