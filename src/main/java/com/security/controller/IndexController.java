package com.security.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.security.config.auth.PrincipalDetails;
import com.security.model.UserDto;
import com.security.service.UserService;

@Controller
public class IndexController {


  private UserService userService; // 필드 주입
  private BCryptPasswordEncoder passwordEncorder;

  // 필드 주입의 단점
  // 테스트 코드 작성 시 정보를 가져올 수 없다

  // 생성자 주입
  public IndexController(UserService userService, BCryptPasswordEncoder passwordEncorder) {
    this.userService = userService;
    this.passwordEncorder = passwordEncorder;
  }

  @GetMapping("/test/login")
  public @ResponseBody String testLogin(Authentication authentication,
      @AuthenticationPrincipal PrincipalDetails userDetails) {// DI(의존성 주입)
    System.out.println("/test/login ========");
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    System.out.println("authentication:" + principalDetails.getDto());

    System.out.println("userDetails:" + userDetails.getUsername());
    return "세션정보 확인하기";
  }

  @GetMapping("/test/oauth/login")
  public @ResponseBody String testOAuthLogin(Authentication authentication,
      @AuthenticationPrincipal OAuth2User oauth) {// DI(의존성 주입)
    System.out.println("/test/oauth/login ========");
    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
    System.out.println("authentication:" + oauth2User.getAttributes());
    System.out.println("oauth2User:" + oauth.getAttributes());
    return "OAuth 세션정보 확인하기";
  }

  // 스프링 시큐리티 자기만의 시큐리티 세션을 가지고있다
  // x라는 클래스를 만들어서 userDetails & OAuth2User 상속받아 사용

  @GetMapping({"", "/"})
  public String index() {
    return "index";
  }

  // OAuth 로그인을 해도 Principal
  @GetMapping("/api/v1/user")
  public @ResponseBody String user(Authentication authentication) {
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    System.out.println("authentication:" + principalDetails.getUsername());
    return "user";
  }

  @GetMapping("/api/v1/admin")
  public @ResponseBody String admin() {
    return "admin";
  }

  @GetMapping("/api/v1/manager")
  public @ResponseBody String manager() {
    return "manager";
  }

  @GetMapping("/loginForm")
  public String loginForm() {
    return "loginForm";
  }

  @GetMapping("/joinForm")
  public String joinForm() {
    return "joinForm";
  }


  @PostMapping("/insertMember")
  public String insertMember(UserDto dto) {
    dto.setPassword(passwordEncorder.encode(dto.getPassword()));
    dto.setRole("ROLE_USER");
    userService.insertMember(dto);
    System.out.println(dto);
    return "redirect:/loginForm";
  }

  @Secured("ROLE_ADMIN")
  @GetMapping("/info")
  public @ResponseBody String info() {
    return "개인정보";
  }

  @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
  @GetMapping("/data")
  public @ResponseBody String data() {
    return "데이터정보";
  }



}
