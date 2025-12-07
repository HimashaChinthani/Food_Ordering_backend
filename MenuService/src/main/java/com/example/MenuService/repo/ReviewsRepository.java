package com.example.MenuService.repo;

import com.example.MenuService.models.ReviewsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<ReviewsModel, Long> {

    // CORRECT — use menuid (NOT menuId)
    List<ReviewsModel> findByMenu_Menuid(Long menuid);

}
