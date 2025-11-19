package com.example.UserService.controllers;

import com.example.UserService.dto.LoginRequest;
import com.example.UserService.dto.UserDTO;
import com.example.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1")
public class UserController {

    @Autowired
    private UserService userService; // inject service

    @GetMapping("/getusers")
    public List<UserDTO> getUsers() {
        return userService.getAllUsers(); // call instance method
    }

    @PostMapping("/adduser")
    public UserDTO saveUser(@RequestBody UserDTO userDTO){
        return userService.saveUser(userDTO);
    }
    @PutMapping("/updateuser")
    public UserDTO updateUser(@RequestBody UserDTO userDTO){
        return userService.saveUser(userDTO);
    }
    @DeleteMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }
    @GetMapping("/getuser/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    @GetMapping("/login")
    public UserDTO login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

}
