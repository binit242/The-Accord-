package com.scm.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.scm.entities.Feedback;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.repsitories.FeedbackRepo;


@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepo feedbackRepository;

    @PostMapping("/submit-feedback")
    public String submitFeedback(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String review,
                                 @RequestParam int rating,
                                 RedirectAttributes redirectAttributes) {

        Feedback feedback = new Feedback();
        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setReview(review);
        feedback.setRating(rating);

        feedbackRepository.save(feedback);
        Message message = Message.builder().content("Your feedback collected !").type(MessageType.green).build();

        return "redirect:/user/feedback";
    }


    @GetMapping("/user/view-feedbacks")
public String viewFeedbacks(Model model) {
    List<Feedback> feedbackList = feedbackRepository.findAll();
    model.addAttribute("feedbacks", feedbackList);
    return "view-feedbacks"; // Create view-feedbacks.html
}

}
