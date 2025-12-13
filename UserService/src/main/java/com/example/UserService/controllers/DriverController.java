package com.example.UserService.controllers;

import com.example.UserService.dto.DriverDTO;
import com.example.UserService.dto.UserDTO;
import com.example.UserService.service.DriverService;
import com.example.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1")
public class DriverController {

    @Autowired
    private DriverService driverService; // inject service

    @GetMapping("/getdrivers")
    public List<DriverDTO> getDrivers() {
        return driverService.getAllDrivers(); // call instance method
    }


    @PostMapping("/adddrivers")
    public DriverDTO saveDriver(@RequestBody DriverDTO driverDTO){
        return driverService.saveDriver(driverDTO);
    }
    @PutMapping("/updatedriver")
    public DriverDTO updateDriver(@RequestBody DriverDTO driverDTO){
        return driverService.updateDriver(driverDTO);
    }
    @DeleteMapping("/deletedriver/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable String id) {
        String result = driverService.deleteDriver(id);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getdriver/{id}")
    public ResponseEntity<DriverDTO> getDriver(@PathVariable String id) {
       DriverDTO driver = driverService.getDriver(id);
        return ResponseEntity.ok(driver); // returns JSON automatically
    }
}
