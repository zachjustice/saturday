package saturday.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import saturday.delegates.AccessTokenDelegate;
import saturday.filters.AuthenticationFilter;
import saturday.services.AccessTokenService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.queries.users-query}")
    private String usersQuery;
    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DataSource dataSource;
    private final AccessTokenDelegate accessTokenDelegate;

    @Autowired
    public WebSecurityConfig(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            @Qualifier("dataSource") DataSource dataSource,
            AccessTokenDelegate accessTokenDelegate
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.dataSource = dataSource;
        this.accessTokenDelegate = accessTokenDelegate;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .authoritiesByUsernameQuery(rolesQuery)
            .dataSource(dataSource)
            .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/register").permitAll()
                .antMatchers(HttpMethod.POST, "/register").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/invite").permitAll()
                .antMatchers(HttpMethod.GET, "/invite").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/confirm_email").permitAll()
                .antMatchers(HttpMethod.PUT, "/confirm_email").permitAll()
                //.antMatchers(HttpMethod.GET, "/send_confirmation_email").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/validate_access_token").permitAll()
                .antMatchers(HttpMethod.POST, "/validate_access_token").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/access_token").permitAll()
                .antMatchers(HttpMethod.PUT, "/access_token").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/send_reset_password_email").permitAll()
                .antMatchers(HttpMethod.POST, "/send_reset_password_email").permitAll()

                .antMatchers(HttpMethod.OPTIONS, "/reset_password").permitAll()
                .antMatchers(HttpMethod.PUT, "/reset_password").permitAll()
                // Authenticate everything else
                .anyRequest().authenticated()
                .and()
                // validate each request using the token in the Authorization header
                .addFilterBefore(
                        new AuthenticationFilter(accessTokenDelegate),
                        BasicAuthenticationFilter.class
                )
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
