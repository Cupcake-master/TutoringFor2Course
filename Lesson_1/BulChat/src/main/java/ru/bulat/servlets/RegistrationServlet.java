package ru.bulat.servlets;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bulat.repository.DatabaseConnection;
import ru.bulat.model.User;
import ru.bulat.utils.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@WebServlet - эта аннотация помогает описывать сервлет.
//В её параметры положили путь, на который он будь срабатывать
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    /**
     * Сервлет является интерфейсом, реализация которого расширяет функциональные возможности сервера.
     * Сервлет взаимодействует с клиентами посредством принципа запрос-ответ.
     * Хотя сервлеты могут обслуживать любые запросы, они обычно используются для расширения веб-серверов.
     * Большинство необходимых для создания сервлетов классов и интерфейсов содержатся в пакетах
     * javax.servlet и javax.servlet.http.
     *
     * Основные методы сервлета:
     * public void init(ServletConfig config) throws ServletException запускается сразу после загрузки сервлета в память;
     * public ServletConfig getServletConfig() возвращает ссылку на объект, который предоставляет доступ к информации о конфигурации сервлета;
     * public String getServletInfo() возвращает строку, содержащую информацию о сервлете, например: автор и версия сервлета;
     * public void service(ServletRequest request, ServletResponse response) throws ServletException, java.io.IOException вызывается для обработки каждого запроса;
     * public void destroy() выполняется перед выгрузкой сервлета из памяти.
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().getRequestDispatcher("/jsp/registration.jsp").forward(request,response);
        Session.getName(request);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rePassword = request.getParameter("rePassword");
        Pattern patternEmail = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcherEmail = patternEmail.matcher(email);
        Optional<User> userOptional = DatabaseConnection.findByEmail(email);
        if (!matcherEmail.matches() || !password.equals(rePassword) || !(password.length() > 6) || !(password.length() < 31
        || userOptional.isPresent())) {
            response.sendRedirect(request.getContextPath() + "/registration");
        }else{
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashPassword = encoder.encode(password);
            User user = DatabaseConnection.save(User.builder()
                    .nickname(nickname)
                    .email(email)
                    .password(hashPassword)
                    .build());
            HttpSession session1 = request.getSession();
            session1.setAttribute("id", user.getId());
            Session.createSession(request, "nickname", nickname);
            Session.createSession(request, "email", email);
            response.sendRedirect(request.getContextPath() + "/chat");
        }
    }

    //Хэширование с помощью Spring Security, а именно производиться взятие пароля и некоторой 'соли'
    //Является более предпочтительным вариантов чем Md5, так как испотльзует ещё и 'соль'
    private String hashingPasswordWithSpringSecurity(String password){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    //Более лёгкое кэширование без использования 'соли'
    private String hashingPasswordWithMd5(String password){
        return DigestUtils.md5Hex(password);
    }
}
