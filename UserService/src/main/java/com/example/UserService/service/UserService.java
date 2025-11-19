package com.example.UserService.service;

import com.example.UserService.dto.UserDTO;
import com.example.UserService.models.UserModel;
import com.example.UserService.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return "User Deleted Successfully";
    }
    public UserDTO getUserById(String id) {
        UserModel userModel = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return modelMapper.map(userModel, UserDTO.class);
    }
    public UserDTO login(String email, String password) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!user.getPassword().equals(password)) { // In production, use hashing!
            throw new RuntimeException("Invalid password");
        }

        return modelMapper.map(user, UserDTO.class);
    }

}
