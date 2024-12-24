package id.winnicode.horizon.model;

import jakarta.validation.constraints.NotBlank;

public class NewsCommentRequest {

    @NotBlank
    String comment;
}
