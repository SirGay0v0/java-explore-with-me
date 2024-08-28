package ru.practicum.service.comment;

import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto create(Long userId, Long eventId, NewCommentDto newCommentDto) throws EntityNotFoundException, EventIsNotPublishedException;

    CommentResponseDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto) throws EntityNotFoundException, CommentException;

    CommentResponseDto getComment(Long id) throws EntityNotFoundException;

    List<CommentResponseDto> getComments(List<Long> usersId, List<Long> eventsId, String text, String startTime, String endTime) throws ValidationException;

    void delete(Long id) throws EntityNotFoundException;
}
