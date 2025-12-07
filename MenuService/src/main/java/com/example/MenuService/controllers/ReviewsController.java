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


}
