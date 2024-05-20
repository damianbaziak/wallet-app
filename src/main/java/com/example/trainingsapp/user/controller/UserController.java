package com.example.trainingsapp.user.controller;

import com.example.trainingsapp.user.dto.UserDTO;
import com.example.trainingsapp.user.entity.User;
import com.example.trainingsapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> userFromDb = userService.getUserByUsername(userDTO.getUsername());

        if (userFromDb.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return new ResponseEntity<>(userService.addUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        Optional<User> user = userService.getUser(id);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity uptadeUser (@PathVariable Long id, @Valid @RequestBody UserDTO userUpdate) {
        User user = userService.updateUser(id, userUpdate);

        return new ResponseEntity<>(user, HttpStatus.OK);


    }




    }
