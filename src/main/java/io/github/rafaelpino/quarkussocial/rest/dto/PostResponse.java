package io.github.rafaelpino.quarkussocial.rest.dto;

import io.github.rafaelpino.quarkussocial.domain.model.Post;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    @CreationTimestamp
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        PostResponse response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }
}
