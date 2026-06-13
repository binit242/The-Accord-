package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.User;
import com.scm.entities.Providers;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.repsitories.FeedbackRepo;
import com.scm.services.ProfilePicService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import com.scm.services.ProfilePicService;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import com.scm.entities.Feedback;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class PageController {

     private Logger logger = org.slf4j.LoggerFactory.getLogger(PageController.class);
   @Autowired
    private UserService userService;

     @Autowired
    private ProfilePicService profilepicService;

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Value("${app.oauth.enabled:false}")
    private boolean oauthEnabled;

    @Value("${app.oauth.google.client-id:}")
    private String googleClientId;

    @Value("${app.oauth.google.client-secret:}")
    private String googleClientSecret;

    @Value("${app.oauth.github.client-id:}")
    private String githubClientId;

    @Value("${app.oauth.github.client-secret:}")
    private String githubClientSecret;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home page handler");
        // sending data to view
        model.addAttribute("name", "Substring Technologies");
        model.addAttribute("youtubeChannel", "Learn Code With Durgesh");
        model.addAttribute("githubRepo", "https://github.com/learncodewithdurgesh/");
        List<Feedback> feedbacks = feedbackRepo.findAll();
        List<Feedback> testimonialTrack;
        List<Feedback> reverseTestimonialTrack;

        if (feedbacks.size() >= 8) {
            int splitIndex = (feedbacks.size() + 1) / 2;
            testimonialTrack = new ArrayList<>(feedbacks.subList(0, splitIndex));
            reverseTestimonialTrack = new ArrayList<>(feedbacks.subList(splitIndex, feedbacks.size()));
        } else {
            testimonialTrack = buildTestimonialTrack(feedbacks, 8);
            reverseTestimonialTrack = new ArrayList<>(testimonialTrack);
            Collections.reverse(reverseTestimonialTrack);
        }

        model.addAttribute("testimonialTrack", testimonialTrack);
        model.addAttribute("reverseTestimonialTrack", reverseTestimonialTrack);
        return "home";
    }

    private List<Feedback> buildTestimonialTrack(List<Feedback> feedbacks, int minimumCards) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return List.of();
        }

        if (feedbacks.size() >= minimumCards) {
            return feedbacks;
        }

        List<Feedback> repeatedFeedbacks = new ArrayList<>(minimumCards);
        for (int index = 0; index < minimumCards; index++) {
            repeatedFeedbacks.add(feedbacks.get(index % feedbacks.size()));
        }
        return repeatedFeedbacks;
    }

    // about route

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    // services

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    // contact page

    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    // this is showing login page
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("googleOAuthEnabled", oauthEnabled && hasText(googleClientId) && hasText(googleClientSecret));
        model.addAttribute("githubOAuthEnabled", oauthEnabled && hasText(githubClientId) && hasText(githubClientSecret));
        return new String("login");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // registration page
    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        // default data bhi daal sakte hai
        // userForm.setName("Durgesh");
        // userForm.setAbout("This is about : Write something about yourself");
        model.addAttribute("userForm", userForm);

        return "register";
    }

    // processing register

    @PostMapping("/do-register")
    public ResponseEntity<?> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("gender") String gender,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic) {
        try {
            if (userService.isUserExistByEmail(email)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
            }

            User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .provider(Providers.SELF)
                .enabled(true)
                .emailVerified(true)
                .build();

            if (profilePic != null && !profilePic.isEmpty()) {
                try {
                    Map<String, Object> uploadResult = profilepicService.uploadProfilepic(
                        profilePic, 
                        email + "-" + System.currentTimeMillis()
                    );

                    if (uploadResult != null && uploadResult.get("secure_url") != null) {
                        user.setProfilePic(uploadResult.get("secure_url").toString());
                        user.setCloudinaryImagePublicId(uploadResult.get("public_id").toString());
                    } else {
                        user.setProfilePic(user.getDisplayProfilePic());
                    }
                } catch (Exception uploadException) {
                    logger.warn("Profile picture upload skipped: {}", uploadException.getMessage());
                    user.setProfilePic(user.getDisplayProfilePic());
                }
            } else {
                user.setProfilePic(user.getDisplayProfilePic());
            }

            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "userId", savedUser.getUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

 @GetMapping("/user/feedback")
public String showFeedbackPage() {
    return "/user/feedback";
}


}
