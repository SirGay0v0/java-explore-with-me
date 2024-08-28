package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.user.User;
import ru.practicum.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public User createUser(User user) {
        return storage.save(user);
    }

    @Override
    public void deleteUser(Long userId) throws NotFoundException {
        if (!storage.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        } else {
            storage.deleteById(userId);
        }
    }

    @Override
    public List<User> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            return storage.findAll(pageable).toList();
        } else {
            return storage.findAllById(ids);
        }
    }
}
