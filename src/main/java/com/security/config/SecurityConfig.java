package com.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.CorsFilter;
import com.security.config.jwt.JwtAuthenticationFilter;
import com.security.config.jwt.JwtAuthorizationFilter;
import com.security.config.oauth.PrincipalOauth2UserService;
import com.security.model.UserMapper;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화,
                                                                    // preAuthorize, postAuthorize
public class SecurityConfig {


  private PrincipalOauth2UserService principalOauth2UserService;
  private CorsFilter corsFilter;
  private UserMapper userMapper;

  public SecurityConfig(CorsFilter corsFilter,
      PrincipalOauth2UserService principalOauth2UserService, UserMapper userMapper) {
    this.corsFilter = corsFilter;
    this.principalOauth2UserService = principalOauth2UserService;
    this.userMapper = userMapper;
  }

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
      AuthenticationManager authenticationManager) throws Exception {
    http.addFilterBefore(corsFilter, ChannelProcessingFilter.class);
    http.csrf(t -> t.disable())
        .sessionManagement((sessionManagement) -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilter(corsFilter) // @CrossOrigin(인증 x), 시큐리티 필터에 등록인증(o)
        .addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManager
        .addFilter(new JwtAuthorizationFilter(authenticationManager, userMapper)) // AuthenticationManager
        .authorizeHttpRequests(t -> t.requestMatchers("/api/v1/user/**").authenticated()
            .requestMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
            .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN").anyRequest().permitAll())
        .formLogin(
            t -> t.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/"))
        .oauth2Login(t -> t.loginPage("/loginForm")
            .userInfoEndpoint(endpoint -> endpoint.userService(principalOauth2UserService)));

    return http.build();
  }

  // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다
  @Bean
  static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
