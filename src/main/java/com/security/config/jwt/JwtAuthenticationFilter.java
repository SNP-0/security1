package com.security.config.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.config.auth.PrincipalDetails;
import com.security.dto.LoginRequestDto;
import com.security.util.JWTOkens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음.
// /login요청해서 username,password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 함
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  // login 요청을하면 로그인 시도를 위해서 실행되는 함수
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    System.out.println("JwtAuthenticationFilter:로그인 시도 중");

    ObjectMapper om = new ObjectMapper();
    LoginRequestDto loginRequestDto = null;


    // username, password 받아서
    try {
      // BufferedReader br = request.getReader();
      //
      // String input = null;
      // while ((input = br.readLine()) != null) {
      // System.out.println(input);
      // }
      loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
      System.out.println("loginRequestDto:" + loginRequestDto);
    } catch (IOException e) {
      e.printStackTrace();
    }

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
            loginRequestDto.getPassword());
    System.out.println("authenticationToken" + authenticationToken);

    // PrincipalDetailsService의 loadUserByUsername() 함수가 실행된 후 정상이면 authentication이 리턴됨
    // DB에 있는 username과 pa
    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    // => 로그인이 되었다는 뜻
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    System.out.println("로그인 완료:" + principalDetails.getDto().getUsername());// 로그인이 정상적으로 되었다는 뜻
    // authentication 객체가 session영역에 저장을 해야하고 그 방법이 return 해주면 됨
    // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는거임
    // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리때문에 session 넣어 줍니다
    return authentication;
  }

  // attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수 실행
  // JWT토큰을 만들어서 request요청한 사용자에게 JWT토큰 response해주면 됨
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    System.out.println("successfulAuthentication 실행 : 인증 완료 뜻");
    PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();


    Map<String, Object> payloads = new HashMap<>();// 사용자 임의 데이타 추가
    long expirationTime = 1000 * 60 * 60 * 3;// 토큰의 만료시간 설정(3시간)
    String token =
        JWTOkens.createToken(principalDetails.getDto().getUsername(), payloads, expirationTime);
    Cookie cookie = new Cookie("Authorization", token);
    response.addCookie(cookie);

  }
}
