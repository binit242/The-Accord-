package com.scm;

import com.scm.config.AppConfig;
import com.scm.entities.Feedback;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repsitories.FeedbackRepo;
import com.scm.repsitories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class Application  implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private FeedbackRepo feedbackRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setName("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword(passwordEncoder.encode("admin"));
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setGender("This is dummy user created initially");
		user.setPhoneVerified(true);

		userRepo.findByEmail("admin@gmail.com").ifPresentOrElse(user1 -> {},() -> {
			userRepo.save(user);
			System.out.println("user created");
		});

		List<Feedback> sampleFeedbacks = List.of(
				createFeedback(
						"Sabo Masties",
						"sabo@example.com",
						"It feels fast, focused, and effortless. I can find every client detail without digging through old notes.",
						5),
				createFeedback(
						"Musharof Chowdhury",
						"musharof@example.com",
						"The interface is clean enough for daily work but polished enough to feel premium.",
						5),
				createFeedback(
						"William Smith",
						"william@example.com",
						"Adding contacts, favorites, and profile data finally feels simple. The dashboard gives me the pulse instantly.",
						5)
		);

		List<Feedback> missingSampleFeedbacks = sampleFeedbacks.stream()
				.filter(feedback -> !feedbackRepo.existsByEmail(feedback.getEmail()))
				.toList();

		if (!missingSampleFeedbacks.isEmpty()) {
			feedbackRepo.saveAll(missingSampleFeedbacks);
			System.out.println("sample feedback testimonials topped up");
		}

	}

	private Feedback createFeedback(String name, String email, String review, int rating) {
		Feedback feedback = new Feedback();
		feedback.setName(name);
		feedback.setEmail(email);
		feedback.setReview(review);
		feedback.setRating(rating);
		return feedback;
	}
}
