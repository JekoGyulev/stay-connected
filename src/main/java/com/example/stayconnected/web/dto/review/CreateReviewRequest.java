package com.example.stayconnected.web.dto.review;

public class CreateReviewRequest {
    private String comment;
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
