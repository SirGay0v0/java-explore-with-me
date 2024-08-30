package ru.practicum.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import ru.practicum.model.requests.ParticipationRequest;
import ru.practicum.model.requests.dto.ParticipationRequestDto;


@org.springframework.context.annotation.Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Регистрация конвертера для LocalDateTime
        modelMapper.addConverter(new StringToLocalDateTimeConverter());

        // Регистрация конвертера для преобразования LocalDateTime в String
        modelMapper.addConverter(new LocalDateTimeToStringConverter());

        // Настройка маппинга для ParticipationRequest -> ParticipationRequestDto
        modelMapper.addMappings(new PropertyMap<ParticipationRequest, ParticipationRequestDto>() {
            @Override
            protected void configure() {
                map().setEvent(source.getEvent().getId());
                map().setRequester(source.getRequester().getId());
            }
        });

        return modelMapper;
    }


}

