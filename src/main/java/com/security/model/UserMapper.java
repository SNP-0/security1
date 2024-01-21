package com.security.model;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

  int insertMember(UserDto dto);

  UserDto findByUsername(String username);

}
