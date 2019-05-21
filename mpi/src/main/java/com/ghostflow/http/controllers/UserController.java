package com.ghostflow.http.controllers;

import com.ghostflow.database.postgres.entities.UserEntity;
import com.ghostflow.services.UserService;
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
    public ResponseEntity create(Principal principal) {
        return ResponseEntity.ok(userService.get(principal.getName()));
    }


    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody UserEntity userEntity) {
        userService.create(userEntity.getEmail(), userEntity.getName(), userEntity.getPassword(), userEntity.getRole());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public ResponseEntity<?> getEmployees(Principal principal,
                                                  @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) long limit,
                                                  @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) long offset) {
        return ResponseEntity.ok(userService.getEmployees(principal.getName(), limit, offset));
    }

    @RequestMapping(value = "/{id}/approve", method = RequestMethod.POST)
    public ResponseEntity<UserEntity> approve(Principal principal, @PathVariable("id") long id) {
        return ResponseEntity.ok(userService.update(principal.getName(), id, true));
    }
}
