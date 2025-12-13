package com.example.MenuService.controllers;


import com.example.MenuService.dto.ReviewsDTO;
import com.example.MenuService.models.ReviewsModel;
import com.example.MenuService.service.ReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v2")
public class ReviewsController {

    @Autowired
    private ReviewsService reviewsService;

    @PostMapping("/addreview")
    public ReviewsModel addReview(@RequestBody ReviewsDTO reviewDto) {
        return reviewsService.addReview(reviewDto);
    }
    @GetMapping("/reviews/{menuid}")
    public List<ReviewsModel> getReviews(@PathVariable Long menuid) {
        return reviewsService.getReviewsByMenuId(menuid);
    }






    @PutMapping("/updatereview/{reviewId}")
    public ReviewsModel updateReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody ReviewsDTO reviewsDTO) {

        // Defensive check: make sure path variable is not null
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID in path must not be null");
        }

        // Set ID in DTO
        reviewsDTO.setReviewId(reviewId);

        // Call service
        return reviewsService.updateReview(reviewsDTO);
    }
    @DeleteMapping("/deletreview/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        return reviewsService.deleteReview(reviewId);
    }
}
