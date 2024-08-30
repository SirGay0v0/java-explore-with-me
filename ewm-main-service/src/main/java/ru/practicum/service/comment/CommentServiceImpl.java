package ru.practicum.service.comment;


import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.State;
import ru.practicum.model.user.User;
import ru.practicum.storage.CommentStorage;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.Constants.formatter;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final CommentStorage commentStorage;

    @Override
    public CommentResponseDto create(Long userId, Long eventId, NewCommentDto newCommentDto) throws EntityNotFoundException, EventIsNotPublishedException {
        Event event = eventStorage.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id " + eventId + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventIsNotPublishedException("Event with id " + eventId + "is not published");
        }
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId +
                " was not found"));
        Comment comment = new Comment()
                .setText(newCommentDto.getText())
                .setAuthor(user)
                .setEvent(event)
                .setTimestamp(LocalDateTime.now());

        return this.commentToResponseDto(commentStorage.save(comment));
    }

    @Override
    public CommentResponseDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto) throws EntityNotFoundException, CommentException {
        Comment comment = commentStorage.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment with id " + commentId + " was not found"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new CommentException("couldn't patch comment as you not author");
        }

        comment.setText(updateCommentDto.getText());
        comment = commentStorage.save(comment);
        return this.commentToResponseDto(comment);
    }

    @Override
    public CommentResponseDto getComment(Long id) throws EntityNotFoundException {
        Comment comment = commentStorage.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment with id " + id + " was not found"));
        return this.commentToResponseDto(comment);
    }

    @Override
    public List<CommentResponseDto> getComments(@Nullable List<Long> usersId, @Nullable List<Long> eventsId,
                                                @Nullable String text, @Nullable String startTime, String endTime) throws ValidationException {

        if (usersId == null) {
            usersId = commentStorage.findUsersId();
        }
        if (eventsId == null) {
            eventsId = commentStorage.findEventsId();
        }
        if (text == null) {
            text = "";
        }

        Optional<String> startOpt = Optional.ofNullable(startTime);
        startTime = startOpt.orElse("2020-01-01 00:00:00");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);

        Optional<String> endOpt = Optional.ofNullable(endTime);
        endTime = endOpt.orElse("2037-01-01 00:00:00");
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);


        if (start.isAfter(end)) {
            throw new ValidationException("start couldn't be after end");
        }

        List<Comment> comments = commentStorage.findComments(usersId, eventsId, text, start, end);
        return comments.stream()
                .map(this::commentToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Comment comment = commentStorage.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment with id " + id +
                " was not found"));
        commentStorage.delete(comment);
    }

    private CommentResponseDto commentToResponseDto(Comment source) {
        return new CommentResponseDto()
                .setAuthor(source.getAuthor().getId())
                .setText(source.getText())
                .setEvent(source.getEvent().getId())
                .setTimestamp(source.getTimestamp().format(formatter));
    }
}
