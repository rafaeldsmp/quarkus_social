package io.github.rafaelpino.quarkussocial.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @NotNull
    private LocalDateTime dateTime = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
