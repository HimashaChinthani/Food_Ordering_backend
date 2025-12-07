package com.example.MenuService.repo;

import com.example.MenuService.models.ReviewsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<ReviewsModel, Long> {

    // Find all reviews by Menu's id (assumes ReviewsModel has a 'menu' field mapped to Menu entity)
    List<ReviewsModel> findByMenu_Menuid(Long menuid);

}
