package com.security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.security.model.UserDto;
import com.security.model.UserMapper;

@Service
public class UserService {

  private UserMapper usermapper;
  private PasswordEncoder passwordEncoder;


  public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.usermapper = userMapper;
    this.passwordEncoder = passwordEncoder;

  }

  @Transactional
  public int insertMember(UserDto dto) {
    String rawPassword = dto.getPassword();
    String encodePassword = passwordEncoder.encode(rawPassword);
    dto.setPassword(encodePassword);
    dto.setRole("ROLE_USER");
    return usermapper.insertMember(dto);
  }


  public UserDto findByUsername(String username) {
    return usermapper.findByUsername(username);
  }



}
