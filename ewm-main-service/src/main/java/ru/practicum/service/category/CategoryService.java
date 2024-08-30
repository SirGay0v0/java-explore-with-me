package ru.practicum.service.category;

import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    Category addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId) throws NotFoundException;

    Category updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException;

    List<Category> getCategories(int from, int size);

    Category getCategory(Long catId) throws NotFoundException, EntityNotFoundException;
}
