package com.example.stayconnected.web.dto.review;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ReviewResponse {
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    private String reviewerUsername;

    public ReviewResponse() {}


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }
}
