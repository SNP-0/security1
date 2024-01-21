package com.security.config.jwt;

import java.io.IOException;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.security.config.auth.PrincipalDetails;
import com.security.model.UserDto;
import com.security.model.UserMapper;
import com.security.util.JWTOkens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 시큐리티가 filter가지고 있는데 그 필터 중에 BasicAuthenticationFilter라는 것이 있음.
// 권한이나 인증이 필요한 특정 주소를 요청했을때 위 필터를 무조건 탄다
// 만약에 권한이 인증이 필요한 주소가 아니라면 이 필터를 안탄다
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private UserMapper userMapper;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
      UserMapper userMapper) {
    super(authenticationManager);
    this.userMapper = userMapper;

  }

  // 인증이나 권한이 필요한 주소요청이 있을때 해당 필터를 타게 됨.
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    // super.doFilterInternal(request, response, chain);
    System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

    String jwtHeader = request.getHeader("Authorization");
    System.out.println("jwtHeader :" + jwtHeader);

    // header가 있는지 확인
    if (jwtHeader == null) {
      chain.doFilter(request, response);
      return;
    }

    // JWT 토큰을 검증해서 정상적인 사용자인지 확인
    String token = request.getHeader("Authorization");
    System.out.println(token);

    Map<String, Object> payload = JWTOkens.getTokenPayloads(token);
    System.out.println("payload:" + payload);

    String username = payload.get("sub").toString();
    System.out.println("username :" + username);
    // 서명이 정상적으로 됨
    if (username != null) {
      UserDto userEntity = userMapper.findByUsername(username);
      PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
      System.out.println("getAuthorities : " + principalDetails.getAuthorities());
      Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,
          null, principalDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(request, response);
  }
}
