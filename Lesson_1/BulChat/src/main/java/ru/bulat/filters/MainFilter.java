package ru.bulat.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(
        urlPatterns = {"/settings/*","/chat", "/feedback", "/developers", "/profile", "/"}
)
//Сервлетный фильтр занимается предварительной обработкой запроса, прежде чем тот попадает в сервлет,
//и/или последующей обработкой ответа, исходящего из сервлета.
public class MainFilter implements Filter {

    //Метод, запускающийся один раз при инициализации фильтра
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("Filter init!");
    }

    /**
     * С помощью своего метода doFilter каждый фильтр получает текущий запрос ServletRequest и ответ ServletResponse,
     * а также FilterChain, содержащий список фильтров, предназначенных для обработки. В методе doFilter фильтр
     * может делать с запросом и ответом всё, что ему захочется - собирать данные или упаковывать объекты для
     * придания им нового поведения. Затем фильтр вызывает chain.doFilter, чтобы передать управление следующему
     * фильтру. После возвращения этого вызова фильтр может по окончании работы своего метода doFilter выполнить
     * дополнительную работу над полученным ответом. К примеру, сохранить регистрационную информацию об этом ответе.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id") == null){
            response.sendRedirect(request.getContextPath() + "/entrance");
        }else{
            filterChain.doFilter(request, response);
        }
    }

    //Метод, запускающийся один раз при уничтожении фильтра
    @Override
    public void destroy() {
        System.out.println("Filter destroy!");
    }
}
