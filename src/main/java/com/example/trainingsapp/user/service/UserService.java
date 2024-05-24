package com.example.trainingsapp.user.service;

import com.example.trainingsapp.user.dto.UserDTO;
import com.example.trainingsapp.user.model.User;

import java.util.Optional;
public interface UserService {

    User addUser(UserDTO userDTO);
    Optional<User> getUser(Long id);
    Optional<User> getUserByUsername(String username);
    User updateUser (Long id, UserDTO userUpdateDTO);
}
