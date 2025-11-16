package com.example.stayconnected.review.model;

import com.example.stayconnected.property.model.Property;
import com.example.stayconnected.user.model.User;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private int rating;
    @Column
    private String comment;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "created_from", nullable = false)
    private User createdFrom;
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    public Review() {}

    public Review(String comment, int rating, User createdFrom, Property property) {
        this.comment = comment;
        this.rating = rating;
        this.createdFrom = createdFrom;
        this.property = property;
        this.createdAt = LocalDateTime.now();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(User createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
