package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;
import ru.practicum.storage.HitStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitStorage storage;
    private final ModelMapper mapper;

    @Override
    public EndpointHitRequestDto saveHit(EndpointHitRequestDto request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(request.getTimestamp(), formatter);
        EndpointHit endpointHit = mapper.map(request, EndpointHit.class);
        endpointHit.setTimestamp(parse);
        return mapper.map(storage.save(endpointHit), EndpointHitRequestDto.class);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from = LocalDateTime.parse(start, formatter);
        LocalDateTime to = LocalDateTime.parse(end, formatter);


        if (unique) {
            List<ViewStats> stats = storage.getUniqueStats(from, to, uris).stream()
                    .map(hit -> mapper.map(hit, ViewStats.class))
                    .collect(Collectors.toList());
            return stats;
        }
        return storage.getStats(from, to, uris);
    }
}
