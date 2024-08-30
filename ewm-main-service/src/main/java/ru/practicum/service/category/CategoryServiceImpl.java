package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.dto.CategoryDto;
import ru.practicum.storage.CategoryStorage;

import java.util.List;

;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryStorage storage;
    private final ModelMapper mapper;

    @Override
    public Category addCategory(CategoryDto categoryDto) {
        Category category = mapper.map(categoryDto, Category.class);
        return storage.save(category);
    }

    @Override
    public void deleteCategory(Long catId) throws NotFoundException {
        if (!storage.existsById(catId)) {
            throw new NotFoundException("Category not found with id " + catId);
        }
        storage.deleteById(catId);
    }

    @Override
    public Category updateCategory(Long catId, CategoryDto categoryDto) throws NotFoundException {
        Category category = storage.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + catId));
        category.setName(categoryDto.getName());
        return storage.save(category);
    }

    @Override
    public List<Category> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return storage.findAll(pageable).toList();
    }

    @Override
    public Category getCategory(Long catId) throws EntityNotFoundException {
        return storage.findById(catId).orElseThrow(() -> new EntityNotFoundException("Category not found with id " + catId));
    }
}
