package com.security.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.type.Alias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Alias("UserDto")
@Builder
public class UserDto {

  private String id;
  private String username;
  private String password;
  private String email;
  private String role;
  private Date createDate;

  private String provider;
  private String providerId;

  @Builder
  public UserDto(String username, String password, String email, String role, Date createDate,
      String provider, String providerId) {

    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.createDate = createDate;
    this.provider = provider;
    this.providerId = providerId;
  }

  public List<String> getRoleList() {
    if (this.role.length() > 0) {
      return Arrays.asList(this.role.split(","));
    }
    return new ArrayList<>();
  }

}
