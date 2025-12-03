package com.example.stayconnected.web.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {
    @NotNull(message = "Comment cannot be null")
    private String comment;
    @Min(1)
    @Max(5)
    private int rating;
}
