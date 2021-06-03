package com.jm.students.controller.rest;


import com.jm.students.DTO.UserDTO;
import com.jm.students.mappers.UserMapper;
import com.jm.students.model.User;
import com.jm.students.model.organization.AbstractOrganization;
import com.jm.students.service.AbstractOrganizationService;
import com.jm.students.service.UserService;
import com.jm.students.service.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;



@RestController
@RequestMapping("api/users")
public class UserRestController {


    private UserService userService;
    private EmailService emailService;
    private AbstractOrganizationService abstractOrganizationService;
    private Environment env;
    private UserMapper userMapper;

    @Autowired
    public UserRestController(UserService userService,
                              EmailService emailService,
                              AbstractOrganizationService abstractOrganizationService,
                              Environment env,
                              UserMapper userMapper) {
        this.userService = userService;
        this.emailService = emailService;
        this.abstractOrganizationService = abstractOrganizationService;
        this.env = env;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody  UserDTO userDTO) {
        if (userService.findUserByEmail(userDTO.getUsername()) == null) {
            User user = userMapper.toUser(userDTO, abstractOrganizationService);
            String password = userService.passwordGenerator();
            user.setPassword(password);
            userService.save(user);
            String message = env.getProperty("email.message");
            emailService.email(user.getUsername(), "Регистрация",
                   message + "\n" + "Ваш логин: "
                           + user.getUsername() + "\n" + "Ваш пароль: " + password);
            return new ResponseEntity<>(new UserDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("IM_USED", HttpStatus.IM_USED);
        }
    }
}

