package com.example.MenuService.controllers;


import com.example.MenuService.dto.MenuDto;
import com.example.MenuService.models.Category;
import com.example.MenuService.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v2")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/getmenu")
    public List<MenuDto> getMenu(){
        return menuService.getAllMenu();
    }
    @PostMapping("/addmenu")
    public MenuDto saveMenu(@RequestBody MenuDto menuDto){
        return menuService.saveMenu(menuDto); // use object, not class name
    }
    @PutMapping("/updatemenu")
    public MenuDto updateMenu(@RequestBody MenuDto menuDto){
        return menuService.saveMenu(menuDto); // use object, not class name
    }
    @DeleteMapping("/deletmenu/{menuid}")
    public String deleteMenu(@PathVariable Long menuid) {
        return menuService.deleteMenu(menuid);
    }

    @GetMapping("/getmenu/{menuid}")
    public MenuDto getMenuById(@PathVariable Long menuid) {
        return menuService.getMenuById(menuid);
    }

    // Get menus by category
    @GetMapping("/category/{category}")
    public List<MenuDto> getMenusByCategory(@PathVariable Category category) {
        return menuService.getMenusByCategory(category);
    }

}
