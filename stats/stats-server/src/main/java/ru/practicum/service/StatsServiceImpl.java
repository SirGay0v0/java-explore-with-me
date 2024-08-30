package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.EndpointHitRequestDto;
import ru.practicum.ViewStats;
import ru.practicum.exceptions.InvalidDateTimeException;
import ru.practicum.storage.HitStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitStorage storage;
    private final ModelMapper mapper;

    @Override
    public String createHit(HttpServletRequest request, EndpointHitRequestDto endpointHitDto) throws InvalidDateTimeException {
        endpointHitDto.setIp(request.getRemoteAddr());
        EndpointHit endpointHit = mapper.map(endpointHitDto, EndpointHit.class);
        LocalDateTime startMoment = LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        endpointHit.setTimestamp(startMoment);
        if (endpointHit.getTimestamp().isAfter(LocalDateTime.now())) {
            throw new InvalidDateTimeException("timestamp must not be in future");
        }
        storage.save(endpointHit);
        return "Информация сохранена";
    }

    @Override
    public List<ViewStats> getStat(String start, String end, List<String> uris, Boolean unique) throws InvalidDateTimeException {
        LocalDateTime startMoment = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endMoment = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (startMoment.isAfter(endMoment)) {
            throw new InvalidDateTimeException("end must not be earlier than start");
        }
        List<ViewStats> viewStatsList;
        if (uris == null && unique.equals(false)) {
            viewStatsList = storage.findRequestWithNullUris(startMoment, endMoment);
            return viewStatsList;
        }
        if (unique.equals(true)) {
            viewStatsList = storage.findUniqueRequest(startMoment, endMoment, uris);
        } else {
            viewStatsList = storage.findRequest(startMoment, endMoment, uris);
        }
        return viewStatsList;
    }
}
