package ru.bulat.repository;

import ru.bulat.model.*;

import java.sql.*;
import java.util.*;

/**
 * Класс взаимодействия с БД, реалтзующий патерн Singleton
 * @author Bulat Bilalov
 * @version 1.0
 */

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Основные этапы работы с базой данных при использовании JDBC:
     * 1) Регистрация драйверов ( Class.forName("org.postgresql.Driver") - 34 строка )
     * 2.jpg) Установление соединения с базой данных (this.connection = DriverManager.getConnection(url, username, password) - 44 строка)
     * 3) Создание запроса(ов) к базе данных;
     * 4) Выполнение запроса(ов) к базе данных;
     * 5) Обработка результата(ов);
     * 6) Закрытие соединения с базой данных.
     */

    /** Конструктор, который формирует connection (соединение) для выполнения запросов */
    public DatabaseConnection() throws SQLException {
        try {
            //С помощью механизма рефлексии ищём класс драйвера
            //JDBC-драйвер, компонент, специфичный для каждой базы данных.
            Class.forName("org.postgresql.Driver");
            //JDBC URL, jdbc - протокол подключения, postgresql - это имя драйвера или имя механизма соединения с базой данных
            //localhost - адрес, 5432 - порт, web_chat - название базы данных
            String url = "jdbc:postgresql://localhost:5432/web_chat";
            //Пароль, который вы указывали при скачивании PostgreSQL
            String password = "543216789";
            //Пользователь, который имеется в PostgreSQL по-умолчанию
            String username = "postgres";
            //DriverManager - позволяет загрузить и зарегистрировать необходимый JDBC-драйвер,
            // а затем получить соединение с базой данных.
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    /**
     * Регистрацию драйвера можно осуществить несколькими способами:
     * 1) java.sql.DriverManager.registerDriver(%объект класса драйвера%);
     * 2.jpg) Class.forName(«полное имя класса драйвера»).newInstance();
     * 3) Class.forName(«полное имя класса драйвера»);
     */

    /**
     * Способы установки соединения с базой данных:
     * Для установки соединения с базой данных используется статический вызов java.sql.DriverManager.getConnection(...)
     * В качестве параметра может передаваться:
     * 1) URL базы данных:
     *    static Connection getConnection(String url);
     * 2.jpg) URL базы данных и набор свойств для инициализации:
     *    static Connection getConnection(String url, Properties info);
     * 3) URL базы данных, имя пользователя и пароль;
     *    static Connection getConnection(String url, String user, String password);
     * В результате вызова будет установлено соединение с базой данных и создан объект
     * класса java.sql.Connection - своеобразная «сессия», внутри контекста которой
     * и будет происходить дальнейшая работа с базой данных.
     */

    /** Метод, который возращает connection */
    private Connection getConnection() {
        return connection;
    }

    /** Метод, который возращает единственный объект класса */
    private static DatabaseConnection getInstance() throws SQLException {
        //Если единственный экземпляр класса ещё не был создан, то он его создаст
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        //Если единственный экземпляр класса был создан, то возратит его
        else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /** Создавайте методы с "говорящими" названиями, чтобы вам было легче понять, что он делает. Также данные название
     используются в более высокоуровневых способах подключения к БД:
     save - сохранение сущности, findById - поиск сущности по Id
     delete and update - используйте названия по предпочтению */
    public static User save(User user) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement("INSERT INTO users(nickname, email, password) VALUES (? , ?, ?) RETURNING id")) {
            settingTheValueForTheRequest(preparedStatement, Arrays.asList(user.getNickname(), user.getEmail(), user.getPassword()));
            user.setId(receivingId(preparedStatement));
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return user;
    }

    public static Optional<User> findByEmail(String email) {
        /**
         * getInstance() - получаем объект класса
         * getConnection() - получаем соединение к БД
         * prepareStatement() - формирование запроса к БД
         * java.sql.Connection - обеспечивает формирование запросов к источнику данных и управление транзакциями.
         * java.sql.Statement , java.sql.PreparedStatement и java.sql.CallableStatement -
         * эти интерфейсы позволяют отправить запрос к источнику данных.
         */

        /**
         * Соединение с базой данной закрывается вызовом метода close() у соответствующего объекта java.sql.Connection
         * или посредством использования механизма try-with-resources при создании такого объекта, появившегося в Java 7.
         * NB! Предварительно необходимо закрыть все запросы созданные этим соединением.
         */
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement("SELECT id, nickname, email, password, information_id FROM users where email = ?")) {
            //Вставляем параметры в запрос вместо '?'
            preparedStatement.setString(1, email.trim().toLowerCase());
            /**
             * Выполнение запросов осуществляется при помощи вызова методов объекта, реализующего интерфейс java.sql.Statement:
             * 1) executeQuery() - для запросов, результатом которых является один набор значений, например запросов SELECT.
             *    Результатом выполнения является объект класса java.sql.ResultSet;
             * 2.jpg) executeUpdate() - для выполнения операторов INSERT, UPDATE или DELETE, а также для операторов
             *    DDL (Data Definition Language). Метод возвращает целое число, показывающее, сколько записей было модифицировано;
             * 3) execute() – исполняет SQL-команды, которые могут возвращать различные результаты.
             *    Например, может использоваться для операции CREATE TABLE. Возвращает true, если первый результат
             *    содержит ResultSet и false, если первый результат - это количество модифицированных записей или
             *    результат отсутствует. Чтобы получить первый результат необходимо вызвать метод getResultSet() или getUpdateCount().
             *    Остальные результаты доступны через вызов getMoreResults(), который при необходимости может быть произведён многократно.
             */

            /**
             * Объект с интерфейсом java.sql.ResultSet хранит в себе результат запроса к базе данных - некий
             * набор данных, внутри которого есть курсор, указывающий на один из элементов набора данных - текущую запись.
             * Используя курсор можно перемещаться по набору данных при помощи метода next().
             * NB! Сразу после получения набора данных его курсор находится перед первой записью и чтобы сделать её текущей необходимо вызвать метод next().
             * Содержание полей текущей записи доступно через вызовы методов getInt(), getFloat(), getString(), getDate() и им подобных.
             */

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return Optional.ofNullable(User.builder()
                        .id(resultSet.getLong("id"))
                        .nickname(resultSet.getString("nickname"))
                        .email(resultSet.getString("email"))
                        .password(resultSet.getString("password"))
                        .information_id(resultSet.getLong("information_id"))
                        .build());
            }
            /**
             * Один из минусов JDBC является выбрасывание SQLException на любое неверное действие, совершенное с БД
             */
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        /**
         * Optional - класс, появившийся в версии 1.8, призванный помочь разработчикам в обработке
         * NullPointerException,  то есть может содержать или не содержать ненулевое значение.
         */
        return Optional.empty();
    }

    /**
     * Чем отличается Statement от PreparedStatement?
     * Statement: используется для простых случаев запроса без параметров.
     * PreparedStatement: предварительно компилирует запрос,
     * который может содержать входные параметры и выполняться несколько раз с разным набором этих параметров.
     * Перед выполнением СУБД разбирает каждый запрос, оптимизирует его и создает «план» (query plan) его выполнения.
     * Если один и тот же запрос выполняется несколько раз, то СУБД в состоянии кэшировать план его выполнения и
     * не производить этапов разборки и оптимизации повторно. Благодаря этому запрос выполняется быстрее.
     * Суммируя: PreparedStatement выгодно отличается от Statement тем, что при повторном использовании с одним
     * или несколькими наборами параметров позволяет получить преимущества заранее прекомпилированного
     * и кэшированного запроса, помогая при этом избежать SQL Injection.
     */

    public static void updatePassword(String email, String newPassword) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement("UPDATE users SET password = ? WHERE email = ?")) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }

    public static Information save(Information information) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("INSERT INTO information(name, surname, patronymic, phone, dateofbirth, gender, country, aboutmyself) values(?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {
            settingTheValueForTheRequest(preparedStatement, Arrays.asList(information.getName(), information.getSurname(),
                    information.getPatronymic(), information.getPhone(), information.getDateOfBirth(), information.getGender(),
                    information.getCountry(), information.getAboutMySelf()));
            information.setId(receivingId(preparedStatement));
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return information;
    }

    public static Feedback save(Feedback feedback) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement("INSERT INTO feedback(name, email, message) values(?, ?, ?) RETURNING id")) {
            settingTheValueForTheRequest(preparedStatement, Arrays.asList(feedback.getName(), feedback.getEmail(), feedback.getMessage()));
            feedback.setId(receivingId(preparedStatement));
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return feedback;
    }

    public static User_Feedback save(User_Feedback user_feedback) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement("INSERT INTO feedback_user(user_id, feedback_id) VALUES (?, ?)")) {
            preparedStatement.setLong(1, user_feedback.getUser_id());
            preparedStatement.setLong(2, user_feedback.getFeedback_id());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return user_feedback;
    }

    public static Group save(Group group) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("INSERT INTO groups(class) VALUES (?) RETURNING id")) {
            settingTheValueForTheRequest(preparedStatement, Collections.singletonList(group.getName()));
            group.setId(receivingId(preparedStatement));
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return group;
    }

    public static User_Group save(User_Group user_group) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("INSERT INTO user_group(user_id, group_id) VALUES (?, ?)")) {
            preparedStatement.setLong(1, user_group.getUser_id());
            preparedStatement.setLong(2, user_group.getGroup_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return user_group;
    }

    public static Optional<Group> findByName(String group) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("SELECT id, class from groups where class = ?")) {
            preparedStatement.setString(1, group);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(Group.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("class"))
                        .build());
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return Optional.empty();
    }

    public static void updateInformation(long idUser, long idInfo) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("UPDATE users set information_id = ? where id = ?")) {
            preparedStatement.setLong(1, idInfo);
            preparedStatement.setLong(2, idUser);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
    }

    public static Optional<Information> findById(long id) {
        try (PreparedStatement preparedStatement = getInstance().getConnection().prepareStatement
                ("SELECT id, name, surname, patronymic, phone, dateofbirth, gender, country from information where id = ?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return Optional.ofNullable(Information.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .surname(resultSet.getString("surname"))
                        .patronymic(resultSet.getString("patronymic"))
                        .phone(resultSet.getString("phone"))
                        .dateOfBirth(resultSet.getString("dateofbirth"))
                        .gender(resultSet.getString("gender"))
                        .country(resultSet.getString("country"))
                        .build());
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
        return Optional.empty();
    }

    private static void settingTheValueForTheRequest(PreparedStatement preparedStatement, List<String> values) throws SQLException {
        for (int i = 1; i <= values.size(); i++) preparedStatement.setString(i, values.get(i-1));
        preparedStatement.execute();
    }

    private static long receivingId(PreparedStatement preparedStatement) throws SQLException {
        ResultSet lastUpdatedId = preparedStatement.getResultSet();
        if (lastUpdatedId.next()) {
            return lastUpdatedId.getInt(1);
        }
        return 0;
    }
}
