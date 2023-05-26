package com.projectx.pay.service;

import com.projectx.pay.entity.ERole;
import com.projectx.pay.entity.Roles;
import com.projectx.pay.entity.User;
import com.projectx.pay.repository.RoleRepository;
import com.projectx.pay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RoleRepository roleRepository;




    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByPhone(String msisdn) {
        return userRepository.findByMsisdn(msisdn);
    }

    public boolean checkIfRegistered(String msisdn){
        return userRepository.findByMsisdn(msisdn).isEmpty()?false:true;
    }

    public Optional <Roles> findByName(ERole role) {
        return roleRepository.findByName(role);
    }



}

