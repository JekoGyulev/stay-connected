package com.example.stayconnected.web.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateReviewRequest {
    @NotBlank
    private String comment;
    @Min(1)
    @Max(5)
    private int rating;

    public CreateReviewRequest() {}

    public CreateReviewRequest(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }

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
}
