package com.example.MenuService.service;

import com.example.MenuService.dto.MenuDto;
import com.example.MenuService.models.Category;
import com.example.MenuService.models.MenuModel;
import com.example.MenuService.repo.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModelMapper modelMapper;

    // GET ALL MENU ITEMS
    public List<MenuDto> getAllMenu() {
        List<MenuModel> menus = menuRepository.findAll();

        return menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .collect(Collectors.toList());
    }
    public MenuDto saveMenu(MenuDto menuDto) {

        MenuModel menuModel = modelMapper.map(menuDto, MenuModel.class);

        MenuModel savedMenu = menuRepository.save(menuModel);

        return modelMapper.map(savedMenu, MenuDto.class);
    }
    public MenuDto updateMenu(MenuDto menuDto) {

        MenuModel menuModel = modelMapper.map(menuDto, MenuModel.class);

        MenuModel updateMenu = menuRepository.save(menuModel);

        return modelMapper.map(updateMenu, MenuDto.class);
    }
    public String deleteMenu(Long menuid) {
        if (!menuRepository.existsById(menuid)) {
            throw new RuntimeException("User not found with id: " + menuid);
        }
        menuRepository.deleteById(menuid);
        return "User Deleted Successfully";
    }
    public MenuDto getMenuById(Long menuid) {
        Optional<MenuModel> menuOpt = menuRepository.findById(menuid);
        if (menuOpt.isPresent()) {
            return modelMapper.map(menuOpt.get(), MenuDto.class);
        } else {
            throw new RuntimeException("Menu not found with ID: " + menuid);
        }
    }

    // Get menus by category
    public List<MenuDto> getMenusByCategory(Category category) {
        List<MenuModel> menus = menuRepository.findByCategory(Category.valueOf(String.valueOf(category)));
        return menus.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .collect(Collectors.toList());
    }


}
