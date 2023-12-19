package io.github.rafaelpino.quarkussocial.rest;

import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.github.rafaelpino.quarkussocial.domain.repository.UserRepository;
import io.github.rafaelpino.quarkussocial.rest.dto.CreateUserRequest;
import io.github.rafaelpino.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON) //essa anotation serve para sinalizar que tipo de dados eu vou receber na requisições da api de usuario
@Produces(MediaType.APPLICATION_JSON) //retornar json na minha resposta
public class UserResource {
    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        //como criamos uma classe para tratar os erros não será necessário esses trecho de código
//        if(!violations.isEmpty()){
//            ConstraintViolation<CreateUserRequest> erro = violations.stream().findAny().get();
//            String errorMessage = erro.getMessage();
//            return Response.status(400).entity(errorMessage).build();
//        }
        if(!violations.isEmpty()){
            ResponseError responseError = ResponseError.createFromValidation(violations);
           //return Response.status(400).entity(responseError).build();
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        repository.persist(user);
        //user.persist();//como é um panacheentity dentro dele ele já faz a persistencia pois a própria entidade tem os métodos para fazer tudo
   //userPersist vai salvar no banco de dados
    //    ex
//        user.delete();p
//        User.delete("delete from User where age <18");

        return Response.status(Response.Status.CREATED.getStatusCode())
                .entity(user).build();
    }

    @GET
    public Response listAllUsers(){

        PanacheQuery<User>  query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user  = repository.findById(id);

        if(user != null){
            repository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        User user  = repository.findById(id);

        if(user != null){
           user.setName(userData.getName());
           user.setAge(userData.getAge());
           //repository.update("alguma coisa"); não é necessário pois tem o @Transactional
           return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
