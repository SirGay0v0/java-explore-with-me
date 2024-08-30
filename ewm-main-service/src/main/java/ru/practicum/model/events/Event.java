package ru.practicum.model.events;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.category.Category;
import ru.practicum.model.location.Location;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Entity
@Table(name = "events")
public class Event {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "annotation")
    String annotation;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Long participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    State state = State.PENDING;

    @Column(name = "created_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    @ManyToOne
    Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    @ManyToOne(targetEntity = Location.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    Location location;
}
