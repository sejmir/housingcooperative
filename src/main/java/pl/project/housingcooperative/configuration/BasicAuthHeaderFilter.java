package pl.project.housingcooperative.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.UserRepository;
import springfox.documentation.builders.PathSelectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class BasicAuthHeaderFilter extends RequestHeaderAuthenticationFilter {
    static final List<String> SECURED_PATH_MATCHERS = List.of(
            "/flats/*/tenants",
            "/users",
            "/users/**"
    );
    public static final String BASIC_PREFIX = "basic ";
    private UserRepository userRepository;

    public BasicAuthHeaderFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        Thread.currentThread().setName("basicAuthHeaderFilter_" + httpRequest.getRequestURI());

        if (!isPathSecuredByBasicAuth(httpRequest.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Authentication authenticationToken = null;
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith(BASIC_PREFIX)) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring(BASIC_PREFIX.length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            if (values.length == 2) {
                Optional<User> userOpt = getAuthenticatedUser(values[0], values[1]);
                authenticationToken = userOpt.isPresent()
                        ? new UsernamePasswordAuthenticationToken(userOpt.get(), "", userOpt.get().getAuthorities())
                        : null;
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(servletRequest, servletResponse);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private boolean isPathSecuredByBasicAuth(String servletPath) {
        return SECURED_PATH_MATCHERS.stream()
                .map(PathSelectors::ant)
                .anyMatch(pathSelector -> pathSelector.apply(servletPath));
    }

    private Optional<User> getAuthenticatedUser(String login, String password) {
        return userRepository.findByMailAndPassword(login, password);
    }
}
