package com.example.UserService.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserModel {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private Role role;
    private Date createdAt;
    private Date updatedAt;
}

