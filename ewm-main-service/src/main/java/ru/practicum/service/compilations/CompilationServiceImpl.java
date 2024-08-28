package ru.practicum.service.compilations;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.model.compilations.Compilation;
import ru.practicum.model.compilations.dto.CompilationResponseDto;
import ru.practicum.model.compilations.dto.NewCompilationDto;
import ru.practicum.model.compilations.dto.UpdateCompilationDto;
import ru.practicum.model.events.Event;
import ru.practicum.storage.CompilationStorage;
import ru.practicum.storage.EventStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationStorage compStorage;
    private final EventStorage eventStorage;
    private final ModelMapper mapper;

    @Override
    public CompilationResponseDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = mapper.map(newCompilationDto, Compilation.class);
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventStorage.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        Compilation savedCompilation = compStorage.save(compilation);
        return mapper.map(savedCompilation, CompilationResponseDto.class);
    }

    @Override
    public CompilationResponseDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compStorage.findById(compId)
                .orElseThrow(() -> new RuntimeException("Compilation not found"));

        this.updateFields(compilation, updateCompilationDto);

        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = new ArrayList<>(eventStorage.findAllByIdIn(updateCompilationDto.getEvents())) {
            };
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compStorage.save(compilation);
        return mapper.map(updatedCompilation, CompilationResponseDto.class);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compStorage.existsById(compId)) {
            throw new RuntimeException("Compilation not found");
        }
        compStorage.deleteById(compId);
    }

    @Override
    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        return compStorage.findAllByPinned(pinned, pageable).stream()
                .map(compilation -> mapper.map(compilation, CompilationResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto getCompilationById(Long compId) {
        Compilation compilation = compStorage.findById(compId)
                .orElseThrow(() -> new RuntimeException("Compilation not found"));
        return mapper.map(compilation, CompilationResponseDto.class);
    }

    private Compilation updateFields(Compilation compilation, UpdateCompilationDto compilationDto) {
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return compilation;
    }
}