package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.service.category.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return service.addCategory(categoryDto);
    }

    @PatchMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,
                                   @RequestBody @Valid CategoryDto categoryDto) throws NotFoundException {
        return service.updateCategory(id, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) throws NotFoundException {
        service.deleteCategory(catId);
    }
}
