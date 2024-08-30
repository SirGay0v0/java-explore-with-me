package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService service;

    @GetMapping
    public List<Category> getCategories(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public Category getCategory(@PathVariable Long catId) throws NotFoundException, EntityNotFoundException {
        return service.getCategory(catId);
    }
}
