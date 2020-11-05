package ru.bulat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Lombok - библиотека, которая позволяет заменять getters, setters, constructors аннотациями
@Data
//@Data - заменяет написание гетеров и сетеров для каждого поля класса
@NoArgsConstructor
//@NoArgsConstructor - заменяет написание конструктора без параметров
@AllArgsConstructor
//@AllArgsConstructor - заменяет написание конструктора со всеми параметрами
@Builder
//@Builder - аннотация, реализующий патерн Builder, позволяющий создание объекта с n количеством параметрами
public class User {
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private Long information_id;
}
