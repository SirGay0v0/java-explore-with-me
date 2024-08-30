package ru.practicum.service.compilations;

import ru.practicum.model.compilations.dto.CompilationResponseDto;
import ru.practicum.model.compilations.dto.NewCompilationDto;
import ru.practicum.model.compilations.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto saveCompilation(NewCompilationDto newCompilationDto);

    CompilationResponseDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);

    void deleteCompilation(Long compId);

    CompilationResponseDto getCompilationById(Long compId);

    List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);
}
