package io.github.rafaelpino.quarkussocial.rest.dto;

import io.github.rafaelpino.quarkussocial.domain.model.Follower;
import io.github.rafaelpino.quarkussocial.rest.FollowerResource;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(){
    }

    public FollowerResponse(Follower follower){
        this(follower.getId(), follower.getFollower().getName());// getfollower e getname para retornar o nome dele o this vai fazer referencia ao construtor followerresponse com this
        //ele constroi os objetos de acordo com o metodo abaixo
    }

    public FollowerResponse(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
