package io.github.rafaelpino.quarkussocial.rest.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class FollowerRequest {
    public Long followerId;
}
