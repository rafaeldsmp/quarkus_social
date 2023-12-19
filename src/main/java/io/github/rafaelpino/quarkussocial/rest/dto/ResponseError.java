package io.github.rafaelpino.quarkussocial.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//classe que representa o objeto de retorno quando tem o erro
@Data
public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;
    private String message;
    private Collection<FieldError> errors; //o array com todos os erros

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations){
    List<FieldError> errors = violations.stream().
            map( cv -> new FieldError(cv //o map vai mapear cada um dos objetos para um objeto que eu quiser que será o objeto do FieldError
                    .getPropertyPath() // retorna o caminho da propriedade (atributo) que causou a violação.
                    .toString(),
                    cv.getMessage())).
            collect //é uma operação terminal que transforma os elementos do fluxo em uma lista.
                    (Collectors.toList());//é um coletor que acumula os elementos do fluxo em uma lista.

        String message = "validation Error";
        var responseError = new ResponseError(message, errors);
        return responseError;
    }

      public Response withStatusCode(int code){
        return  Response.status(code).entity(this).build(); //está construindo um objeto de resposta usando o código de status fornecido (code) e definindo a entidade da resposta como a própria instância do objeto atual (this).
    }
}
