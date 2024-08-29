package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.events.Status;
import ru.practicum.model.requests.ParticipationRequest;

import java.util.List;
import java.util.Map;

@Repository
public interface RequestStorage extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT COUNT(p) " +
            "FROM ParticipationRequest p " +
            "WHERE p.event.id = :id AND p.status = 'CONFIRMED'")
    Long countConfirmedRequests(@Param("id") Long eventId); // подсчет уже подтвержденных запросов


    @Query("SELECT r.event.id, COUNT(r.id) " +
            "FROM ParticipationRequest r " +
            "WHERE r.status = :status AND r.event.id IN :eventIds " +
            "GROUP BY r.event.id")
    List<Object[]> countConfirmedRequestsForEvents(@Param("status") Status status,
                                                   @Param("eventIds") List<Long> eventIds);


    @Query("SELECT COUNT (p) " +
            "FROM ParticipationRequest p " +
            "WHERE p.event.id = :id")
    Long countRequests(@Param("id") Long eventId); // подсчет общего количества заявок на событие

    ParticipationRequest findByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);


}
