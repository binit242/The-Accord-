package com.scm.entities;

import jakarta.persistence.*;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(length = 1000)
    private String review;

    private int rating;

    // Getters and setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return "AC";
        }

        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String part : parts) {
            if (!part.isBlank() && initials.length() < 2) {
                initials.append(Character.toUpperCase(part.charAt(0)));
            }
        }

        return initials.length() == 0 ? "AC" : initials.toString();
    }
}
