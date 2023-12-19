package io.github.rafaelpino.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank (message = "name is required") //vai verificar se a tring é nula ou vazia
    private String name;
    @NotNull (message = "Age is Required")
    private Integer age;

}
