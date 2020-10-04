package resourceDisplay;

import org.springframework.stereotype.Controller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/**
 * This Class is the Main class it is also used for configuration of spring security and spring session
 */
@SpringBootApplication
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds=3600)
@Controller
public class Login extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Login.class, args);
    }
    /**
     * This configures which mappings need authentication and which don't
     * @param http - Used for http connection
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login().defaultSuccessUrl("/index", true);
        // @formatter:on
    }

}