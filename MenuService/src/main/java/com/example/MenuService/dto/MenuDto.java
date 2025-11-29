package com.example.MenuService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

    private Long menuid;
    private String name;
    private String description;
    private byte[] image;
    private double price;
    private int quantity;
    private String category;
    private boolean available;
}
