package com.example.MenuService.service;

import com.example.MenuService.dto.ReviewsDTO;
import com.example.MenuService.models.MenuModel;
import com.example.MenuService.models.ReviewsModel;
import com.example.MenuService.repo.MenuRepository;
import com.example.MenuService.repo.ReviewsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewsService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ReviewsModel addReview(ReviewsDTO reviewDto) {
        // Map simple fields from DTO (rating, comment, userId)
        ReviewsModel review = modelMapper.map(reviewDto, ReviewsModel.class);

        // Fetch Menu entity using menuId from DTO
        MenuModel menu = menuRepository.findById(reviewDto.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + reviewDto.getMenuId()));

        review.setMenu(menu); // set the actual Menu entity
        review.setUserId(reviewDto.getUserId());

        return reviewsRepository.save(review);
    }
    public List<ReviewsModel> getReviewsByMenuId(Long menuid) {

        // Check if the menu exists
        menuRepository.findById(menuid)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuid));

        // Fetch reviews by matching menu -> menuid
        return reviewsRepository.findByMenu_Menuid(menuid);
    }

}
