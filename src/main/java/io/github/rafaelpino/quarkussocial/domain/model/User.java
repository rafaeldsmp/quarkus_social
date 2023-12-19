package io.github.rafaelpino.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "tb_user")
@Data
public class User {//panache entity simplifica a definição de entidades tornando fácil criar,recuperar,atualizar e excluir registros de um bd    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

}
