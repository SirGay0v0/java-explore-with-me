package ru.practicum.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exceptions.CommentException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.EventIsNotPublishedException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.model.comment.dto.CommentResponseDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.comment.dto.UpdateCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/event/{eventId}")
    public CommentResponseDto create(@PathVariable(name = "userId") Long userId,
                                     @PathVariable(name = "eventId") Long eventId, @Valid @RequestBody
                                     NewCommentDto newCommentDto) throws
            EventIsNotPublishedException, EntityNotFoundException {
        return service.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{userId}" + "/comment" + "/{commentId}")
    public CommentResponseDto patch(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "commentId") Long commentId, @Valid @RequestBody
                                    UpdateCommentDto updateCommentDto) throws
            EntityNotFoundException, CommentException {
        return service.update(userId, commentId, updateCommentDto);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDto getComment(@PathVariable(name = "commentId") Long commentId) throws EntityNotFoundException {
        return service.getComment(commentId);
    }

    @GetMapping
    public List<CommentResponseDto> get(@RequestParam(required = false) List<Long> userId,
                                        @RequestParam(required = false) List<Long> eventId,
                                        @RequestParam(required = false) String text,
                                        @RequestParam(required = false) String startTime,
                                        @RequestParam(required = false) String endTime) throws ValidationException {
        return service.getComments(userId, eventId, text, startTime, endTime);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable(name = "commentId") Long commentId) throws EntityNotFoundException {
        service.delete(commentId);
    }

}
