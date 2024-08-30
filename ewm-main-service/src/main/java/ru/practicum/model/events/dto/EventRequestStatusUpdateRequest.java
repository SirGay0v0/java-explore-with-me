package ru.practicum.model.events.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;
    String status;
}
