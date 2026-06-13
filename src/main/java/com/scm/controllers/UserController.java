package com.scm.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ProfilePicService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ProfilePicService profilepicService;

    // user dashboard page
  @RequestMapping(value = "/dashboard")
public String userDashboard(Model model, Authentication authentication) {
    System.out.println("User dashboard");

    String email = Helper.getEmailOfLoggedInUser(authentication);

    System.out.println("Authenticated email: " + email);

    User user = userService.getUserByEmail(email);
    if (user == null) {
        model.addAttribute("favoriteCount", 0L);
        model.addAttribute("totalContactCount", 0L);
        model.addAttribute("error", "User not found for email: " + email);
         model.addAttribute("favoriteContacts", Collections.emptyList());
        model.addAttribute("error", "User not found for email: " + email);
        return "user/dashboard";
    }
    long favoriteCount = contactService.countFavoriteContactsByUserId(user.getUserId());
    long totalContactCount = contactService.countContactsByUserId(user.getUserId());
    long socialLinkCount = contactService.countContactsWithAnySocialLink(user.getUserId());
    model.addAttribute("socialLinkCount", socialLinkCount);
    model.addAttribute("favoriteCount", favoriteCount);
    model.addAttribute("totalContactCount", totalContactCount);
    List<Contact> favoriteContacts = contactService.getFavoriteContactsByUserId(user.getUserId());
    model.addAttribute("favoriteContacts", favoriteContacts);
    return "user/dashboard";
}

    // user profile page
    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {
        return "user/profile";
    }



// View Update Form
@GetMapping("/profile/view/{userId}")
public String updateUserForm(@PathVariable("userId") String userId, Model model, HttpSession session) {
    Optional<User> optionalUser = userService.getUserById(userId);
    if (optionalUser.isEmpty()) {
        session.setAttribute("message", Message.builder().content("User not found!").type(MessageType.red).build());
        return "redirect:/user/profile";
    }

    User user = optionalUser.get();

    UserForm userForm = UserForm.builder()
            .name(user.getName())
            .email(user.getEmail())
            .gender(user.getGender())
            .phoneNumber(user.getPhoneNumber())
            .build();

    model.addAttribute("userForm", userForm);
    model.addAttribute("userId", userId);
    model.addAttribute("profilePicUrl", user.getDisplayProfilePic());
    return "user/update-profile";
}

// Update User
@PostMapping("/profile/update/{userId}")
public String updateUser(@PathVariable("userId") String userId,
                         @ModelAttribute("userForm") UserForm userForm,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {

    if (bindingResult.hasErrors()) {
        model.addAttribute("userId", userId);
        return "user/update-profile";
    }

    Optional<User> optionalUser = userService.getUserById(userId);
    if (optionalUser.isEmpty()) {
        session.setAttribute("message", Message.builder().content("User not found!").type(MessageType.red).build());
        return "redirect:/user/profile";
    }

    User user = optionalUser.get();
    user.setName(userForm.getName());
    user.setEmail(userForm.getEmail());
    user.setPhoneNumber(userForm.getPhoneNumber());
    user.setGender(userForm.getGender());

    // Handle profile picture upload
    if (userForm.getProfilePic() != null && !userForm.getProfilePic().isEmpty()) {
        var uploadResult = profilepicService.uploadProfilepic(userForm.getProfilePic(), userId);
        if (uploadResult != null && uploadResult.get("secure_url") != null) {
            user.setProfilePic(uploadResult.get("secure_url").toString());
            user.setCloudinaryImagePublicId(uploadResult.get("public_id").toString());
        }
    }

    userService.update(user);
    session.setAttribute("message", Message.builder().content("User Updated!").type(MessageType.green).build());

    return "redirect:/user/profile";
}


   
}


   
    
