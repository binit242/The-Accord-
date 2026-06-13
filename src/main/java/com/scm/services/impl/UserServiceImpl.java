package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.repsitories.UserRepo;

import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

   

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private  Helper helper;

    @Override
    public User saveUser(User user) {
        // user id : have to generate
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        // password encode
        // user.setPassword(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set the user role

        user.setRoleList(List.of(AppConstants.ROLE_USER));

        logger.info(user.getProvider().toString());
        
        User savedUser = userRepo.save(user);
        
        
        return savedUser;

    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

   @Override
public User update(User user) {
    User existing = userRepo.findById(user.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    existing.setName(user.getName());
    existing.setEmail(user.getEmail());
    existing.setGender(user.getGender());
    existing.setPhoneNumber(user.getPhoneNumber());
    existing.setProfilePic(user.getProfilePic());
    existing.setEnabled(user.isEnabled());
    existing.setEmailVerified(user.isEmailVerified());
    existing.setPhoneVerified(user.isPhoneVerified());
    existing.setProvider(user.getProvider());
    existing.setProviderUserId(user.getProviderUserId());

    return userRepo.save(existing);
}


    

    @Override
    public boolean isUserExist(String userId) {
        User user2 = userRepo.findById(userId).orElse(null);
        return user2 != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);

    }



 

    @Override
    public void delete(String userId) {
        userRepo.deleteById(userId);
    }


}
