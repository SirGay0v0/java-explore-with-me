package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.compilations.Compilation;

import java.util.List;

@Repository
public interface CompilationStorage extends JpaRepository<Compilation, Long> {
    @Query(value = "SELECT c " +
            "FROM Compilation c " +
            "WHERE (c.pinned IS NOT NULL OR c.pinned = :pinned)")
    List<Compilation> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
