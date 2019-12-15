package com.ghostflow.http.controllers;

import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static com.ghostflow.http.beans.PaginationParameters.DEFAULT_LIMIT;
import static com.ghostflow.http.beans.PaginationParameters.DEFAULT_OFFSET;

@Slf4j
@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(Principal principal) {
        return ResponseEntity.ok(userService.getUserInfo(principal.getName()));
    }


    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {
        userService.createUser(userEntity.getEmail(), userEntity.getName(), userEntity.getPassword());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/employees/signup", method = RequestMethod.POST)
    public ResponseEntity<?> createEmployee(@RequestBody UserEntity userEntity) {
        userService.createEmployee(userEntity.getEmail(), userEntity.getName(), userEntity.getPassword());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public ResponseEntity<?> getEmployees(Principal principal,
                                                  @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) long limit,
                                                  @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) long offset) {
        return ResponseEntity.ok(userService.getEmployees(principal.getName(), limit, offset));
    }

    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    @Getter
    private static class UserRole {
        private final UserEntity.Role role;
    }

    @RequestMapping(value = "/{id}/role", method = RequestMethod.POST)
    public ResponseEntity<UserEntity> approve(Principal principal, @PathVariable("id") long id, @RequestBody UserRole role) {
        return ResponseEntity.ok(userService.approve(principal.getName(), id, role.getRole()));
    }

    @RequestMapping(value = "/{id}/role", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(Principal principal, @PathVariable("id") long id) {
        userService.delete(principal.getName(), id);
        return ResponseEntity.ok().build();
    }
}
