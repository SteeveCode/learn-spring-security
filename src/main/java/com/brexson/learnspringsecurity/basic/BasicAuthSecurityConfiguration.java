package com.brexson.learnspringsecurity.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class BasicAuthSecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {auth.anyRequest().authenticated();});
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //http.formLogin();
        http.httpBasic(withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.headers().frameOptions().sameOrigin(); // h2-console requires frame option permission

        return http.build();
    }
    @Bean public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer(){
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedOrigins("http://localhost:3000");
            }
        };
    }
//    @Bean
//    public UserDetailsService userDetailsService(){
//        var user = User.withUsername("in28ms")
//                .password("{noop}dummy")
//                .roles("USER")
//                .build();
//        var admin = User.withUsername("admin")
//                .password("{noop}dummy")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }
    @Bean // selects h2 as embedded datasource & also runs script to create Authorities & Users Schemas
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }
    @Bean // specifies datasource, creates users, inserts users into schemas
    public UserDetailsService userDetailsService(DataSource dataSource){
        var user = User.withUsername("in28ms")
                //.password("{noop}dummy")
                .password("dummy")
                .passwordEncoder(str->passwordEncoder().encode(str))
                .roles("USER")
                .build();
        var admin = User.withUsername("admin")
                //.password("{noop}dummy")
                .password("dummy")
                .passwordEncoder(str->passwordEncoder().encode(str))
                .roles("ADMIN", "USER")
                .build();
       var jdbcUserDetailsManager =  new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(user);
        jdbcUserDetailsManager.createUser(admin);

        return jdbcUserDetailsManager;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
