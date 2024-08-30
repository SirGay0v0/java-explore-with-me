package ru.practicum.service.user;

import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.user.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    void deleteUser(Long userId) throws NotFoundException;

    List<User> getUsers(List<Long> ids, int from, int size);
}
