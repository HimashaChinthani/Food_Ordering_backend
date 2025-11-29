package com.example.UserService.controllers;

import com.example.UserService.dto.LoginRequest;
import com.example.UserService.dto.UserDTO;
import com.example.UserService.models.UserModel;
import com.example.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        String result = userService.deleteUser(id);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getuser/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        UserDTO user = userService.getUser(id);
        return ResponseEntity.ok(user); // returns JSON automatically
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        UserModel user = userService.login(loginRequest);

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        return ResponseEntity.ok(user);
    }

}