package com.example.globalkineticapi.dto;

import com.example.globalkineticapi.model.User;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {
    List<User> users;
}
