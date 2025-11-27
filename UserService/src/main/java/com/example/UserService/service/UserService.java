package com.example.UserService.service;

import com.example.UserService.dto.LoginRequest;
import com.example.UserService.dto.UserDTO;
import com.example.UserService.models.UserModel;
import com.example.UserService.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private  UserRepository userRepository; // inject the repository

    @Autowired
    private ModelMapper modelMapper; // inject ModelMapper

    // non-static method
    public List<UserDTO> getAllUsers() {
        List<UserModel> users = userRepository.findAll();
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

    public UserDTO saveUser(UserDTO userDTO) {
        UserModel userModel = modelMapper.map(userDTO, UserModel.class);
        UserModel savedUser = userRepository.save(userModel);
        return modelMapper.map(savedUser, UserDTO.class);
    }
    public UserDTO updateUser(UserDTO userDTO) {
        UserModel userModel = modelMapper.map(userDTO, UserModel.class);
        UserModel savedUser = userRepository.save(userModel);
        return modelMapper.map(savedUser, UserDTO.class);
    }
    public String deleteUser(String id) {
        id = id.trim();  // double-safe
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return "User Deleted Successfully";
    }
    public UserDTO getUser(String id) {
        id = id.trim();  // double-safe


        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }


        UserModel userModel = userRepository.findById(id).get(); // safe because existsById returned true

        return modelMapper.map(userModel, UserDTO.class);
    }




    public UserModel login(LoginRequest loginRequest) {

        // Find user by email (Optional handling)
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> user.getPassword().equals(loginRequest.getPassword()))
                .orElse(null);
    }





}