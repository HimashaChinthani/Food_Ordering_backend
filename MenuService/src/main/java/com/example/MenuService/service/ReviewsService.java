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
    public ReviewsModel updateReview(ReviewsDTO reviewsDTO) {
        Long id = reviewsDTO.getReviewId();
        if (id == null) {
            throw new IllegalArgumentException("Review ID must not be null");
        }

        ReviewsModel existingReview = reviewsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        existingReview.setRating(reviewsDTO.getRating());
        existingReview.setComment(reviewsDTO.getComment());

        return reviewsRepository.save(existingReview);
    }
    public String deleteReview(Long reviewId) {
        if (!reviewsRepository.existsById(reviewId)) {
            throw new RuntimeException("review not found with id: " + reviewId);
        }
        reviewsRepository.deleteById(reviewId);
        return "Review Deleted Successfully";
    }

}
