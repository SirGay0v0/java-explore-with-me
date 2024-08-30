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
import ru.practicum.model.compilations.dto.CompilationResponseDto;
import ru.practicum.model.compilations.dto.NewCompilationDto;
import ru.practicum.model.compilations.dto.UpdateCompilationDto;
import ru.practicum.service.compilations.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto saveCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return service.saveCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(
            @PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationDto updateCompilationDto) {
        return service.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }
}
