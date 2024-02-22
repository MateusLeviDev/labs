package demo.com.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 1 filtro BasicAuthenticationFilter basic64
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginGeneratingFilter
     * DefaultLogoutGeneratingFilter
     * FilterSecurityInterceptor
     * Authentication -> Authorization
     *
     * @param http
     * @param userDetailsManager
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InMemoryUserDetailsManager userDetailsManager) throws Exception {
        http.csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
    //O token CSRF (XSRF-Token) é geralmente usado para proteger contra ataques CSRF
    // (Cross-Site Request Forgery).

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails user = User.withUsername("Levi")
                .password(encoder.encode("user_password"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("Mateus")
                .password(encoder.encode("admin_password"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

}
