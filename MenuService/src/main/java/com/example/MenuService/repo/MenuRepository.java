package com.example.MenuService.repo;

import com.example.MenuService.models.Category;
import com.example.MenuService.models.MenuModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<MenuModel, Long> {

    List<MenuModel> findByCategory(Category category);

    List<MenuModel> findByNameContainingIgnoreCase(String name);
}
