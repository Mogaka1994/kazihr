package com.projectx.pay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectx.pay.entity.ERole;
import com.projectx.pay.entity.Roles;
import com.projectx.pay.entity.User;
import com.projectx.pay.globalFunctions.GlobalFunctions;
import com.projectx.pay.model.UserModel;
import com.projectx.pay.service.CrudService;
import com.projectx.pay.service.UserService;
import com.projectx.pay.utils.ResourceNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.projectx.pay.globalFunctions.GlobalFunctions.generatePass;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Api(tags = "Kazi Clients")
@RequestMapping("/api/test")
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    CrudService crudService;

    Map<String, Object> responseMap = new HashMap<>();

    Set<Roles> roles = new HashSet<>();

    @Autowired
    PasswordEncoder encoder;


    @ApiOperation(value = "Create users of Kazi HR.")
    @PostMapping("/create_user")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody UserModel model) throws JsonProcessingException {
        User user = new User();
        try {
//            String passwd = new GlobalFunctions().hmacDigest(generatePass());
            user.setFirstname(model.getFirstname());
            user.setLastname(model.getLastname());
            user.setEmail(model.getEmail());
            user.setUsername(model.getUsername());
            user.setPassword(model.getPassword());
            user.setAdmin(model.getAdmin());
            user.setMsisdn(model.getMsisdn());
            user.setPassword(encoder.encode(generatePass()));
            user.setStatus(1);
            user.setFirst_login(1);
            if (!userService.checkIfRegistered(model.getMsisdn())) {
                if (model.getRole() == null) {
                    Roles userRole = userService.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                } else {
                    model.getRole().forEach(role -> {
                        switch (role) {
                            case "admin":
                                Roles adminRole = userService.findByName(ERole.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(adminRole);

                                break;
                            case "mod":
                                Roles modRole = userService.findByName(ERole.ROLE_MODERATOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(modRole);

                                break;
                            default:
                                Roles userRole = userService.findByName(ERole.ROLE_USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(userRole);
                        }
                    });
                    user.setRoles(roles);
                }
                userService.saveUser(user);
                responseMap.put("responseCode", "00");
                responseMap.put("responseMessage", "Successfully registered");
            } else {
                responseMap.put("responseCode", "01");
                responseMap.put("responseMessage", "User exists registered");
            }
        } catch (Exception e) {
            responseMap.put("responseCode", "01");
            responseMap.put("responseMessSage", "Error processing request");
        }
        return new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap), HttpStatus.OK);

    }

    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @PostMapping("/get_user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@RequestBody UserModel model) throws ResourceNotFoundException {
        User user = userService.findByPhone(model.getMsisdn())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + model.getMsisdn()));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update_user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User>updateUser(@RequestBody UserModel model) throws ResourceNotFoundException {
        User user = userService.findByPhone(model.getMsisdn())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + model.getMsisdn()));

        user.setEmail(model.getEmail());
        user.setPassword(model.getPassword());
        user.setUsername(model.getUsername());
        user.setFirstname(model.getFirstname());

        if (model.getRole() == null) {
            Roles userRole = userService.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            model.getRole().forEach(role -> {
                switch (role) {
                    case "admin":
                        Roles adminRole = userService.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Roles modRole = userService.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Roles userRole = userService.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
            user.setRoles(roles);
        }
        final User updateduser = userService.saveUser(user);
        return ResponseEntity.ok(updateduser);
    }
}
