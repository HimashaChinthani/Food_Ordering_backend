package com.example.UserService.service;

import com.example.UserService.dto.DriverDTO;

import com.example.UserService.models.DriverModel;

import com.example.UserService.repo.DriverRepository;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository; // inject the repository

    @Autowired
    private ModelMapper modelMapper; // inject ModelMapper

    // non-static method
    public List<DriverDTO> getAllDrivers() {
        List<DriverModel> drivers = driverRepository.findAll();
        return modelMapper.map(drivers, new TypeToken<List<DriverDTO>>() {}.getType());
    }

    public DriverDTO saveDriver(DriverDTO driverDTO) {
        DriverModel driverModel = modelMapper.map(driverDTO, DriverModel.class);
        DriverModel savedDriver = driverRepository.save(driverModel);
        return modelMapper.map(savedDriver, DriverDTO.class);
    }
    public DriverDTO updateDriver(DriverDTO driverDTO) {
        DriverModel  driverModel= modelMapper.map(driverDTO, DriverModel.class);
        DriverModel savedDriver = driverRepository.save(driverModel);
        return modelMapper.map(savedDriver, DriverDTO.class);
    }
    public String deleteDriver(String id) {
        id = id.trim();  // double-safe
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
        return "Driver Deleted Successfully";
    }
    public DriverDTO getDriver(String id) {
        id = id.trim();  // double-safe


        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found with id: " + id);
        }


     DriverModel driverModel = driverRepository.findById(id).get(); // safe because existsById returned true

        return modelMapper.map(driverModel, DriverDTO.class);
    }




}
