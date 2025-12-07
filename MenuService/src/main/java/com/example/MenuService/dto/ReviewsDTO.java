package com.example.MenuService.dto;

import com.example.MenuService.models.MenuModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsDTO {
    private Long reviewId;
    private int rating;
    private String comment;
    private Long menuId;   // just the ID of the menu
    private String userId; // user ID from User service


}
