package ru.bulat.service;

import ru.bulat.model.User;

import java.util.Optional;

//Реализация сервиса, который является промежуточным звеном между controllers (servlets) и repository, добавляя
//некоторый слой абстакции. В дальнейшем при изучении фраймворка Spring вы изучите services и repository
//Реализация CRUD (create, read, update, delete)
public interface UserService {

    //Сохранение сущности (create)
    User save(User user);

    //Поиск сущности по некоторому (уникальному) параметру (read)
    Optional<User> findByEmail(String email);

    //Обновление сущности (update)
    void updatePassword(String email, String newPassword);

    //Удаление сущности (delete)
    void delete(String email);
}
