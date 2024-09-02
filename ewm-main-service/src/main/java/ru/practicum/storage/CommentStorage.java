package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.comment.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    @Query("SELECT DISTINCT c.author.id " +
            "FROM Comment c")
    List<Long> findUsersId();

    @Query("SELECT DISTINCT c.event.id " +
            "FROM Comment c")
    List<Long> findEventsId();

    @Query("SELECT c FROM Comment c " +
            "WHERE (c.author.id IN :users) AND (c.event.id IN :events) AND (LOWER(c.text) " +
            "LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND c.timestamp BETWEEN :start AND :end")
    List<Comment> findComments(@Param("users") List<Long> userId,
                               @Param("events") List<Long> eventId,
                               @Param("text") String text,
                               @Param("start") LocalDateTime startTime,
                               @Param("end") LocalDateTime endTime);
}
