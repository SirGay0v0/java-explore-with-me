package ru.practicum.storage;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.user.User;

@Repository
public interface UserStorage extends JpaRepository<User, Long> {

}
