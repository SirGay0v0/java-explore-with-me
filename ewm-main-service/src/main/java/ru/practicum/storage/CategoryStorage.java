package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.category.Category;

@Repository
public interface CategoryStorage extends JpaRepository<Category, Long> {
}
