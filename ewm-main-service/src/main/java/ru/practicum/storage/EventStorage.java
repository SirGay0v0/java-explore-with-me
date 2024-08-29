package ru.practicum.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.events.Event;
import ru.practicum.model.events.State;
import ru.practicum.model.events.dto.EventShortDtoDb;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventStorage extends JpaRepository<Event, Long> {
    @Query("SELECT new ru.practicum.model.events.dto.EventShortDtoDb(e.annotation, e.category, 0L, e.eventDate, e.id, e.initiator, e.paid, e.title, 0L) " +
            "FROM Event e " +
            "WHERE e.initiator = :initiator")
    List<EventShortDtoDb> findAllByInitiator(@Param("initiator") User initiator, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    @Query("select e from Event e")
    List<Event> findByEmptyParameters(Pageable pageable);

    @Query(value = "select e from Event e where (e.initiator.id IN :usersId) AND " +
            "(e.state IN :states) AND (e.category.id IN :categoriesId) " +
            "order by e.eventDate desc")
    List<Event> findEventByUsersAndStateAndCategory(@Param("usersId") List<Long> usersId,
                                                    @Param("states") List<State> states,
                                                    @Param("categoriesId") List<Long> categoriesId, Pageable pageable);

    @Query(value = "select e from Event e where (e.initiator.id IN :usersId) AND " +
            "(e.state IN :states) AND (e.category.id IN :categoriesId) AND " +
            "e.eventDate BETWEEN :start AND :end order by e.eventDate")
    List<Event> findEventByUsersAndStateAndCategoryBetween(@Param("usersId") List<Long> usersId, @Param("states") List<State> states,
                                                           @Param("categoriesId") List<Long> categoriesId,
                                                           @Param("start") LocalDateTime startTime,
                                                           @Param("end") LocalDateTime endTime, Pageable pageable);

    @Query(value = "select e " +
            "from Event e " +
            "where LOWER(e.annotation) like LOWER(CONCAT('%', :text, '%')) AND e.category.id IN :categoriesId " +
            "AND (:paid IS NULL OR e.paid = :paid) AND e.state = 'PUBLISHED' " +
            "order by e.eventDate desc")
    List<Event> findEventsByAllCriteriaWithoutTime(@Param("text") String textAnnotation,
                                                   @Param("categoriesId") List<Long> categoriesId,
                                                   @Param("paid") Boolean paid, Pageable pageable);

    @Query(value = "select e " +
            "from Event e " +
            "where LOWER(e.annotation) like LOWER(:text) AND e.category.id IN :categoriesId " +
            "AND (:paid IS NULL OR e.paid = :paid) AND e.state = 'PUBLISHED' AND e.eventDate BETWEEN :start AND :end " +
            "order by e.eventDate desc")
    List<Event> findEventsByAllCriteria(@Param("text") String textAnnotation,
                                        @Param("categoriesId") List<Long> categoriesId,
                                        @Param("paid") Boolean paid, @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end, Pageable pageable);
}

