package io.github.rafaelpino.quarkussocial.rest;

import io.github.rafaelpino.quarkussocial.domain.model.Follower;
import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.github.rafaelpino.quarkussocial.domain.repository.FollowerRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.UserRepository;
import io.github.rafaelpino.quarkussocial.rest.dto.FollowerRequest;
import io.github.rafaelpino.quarkussocial.rest.dto.FollowerResponse;
import io.github.rafaelpino.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {
    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository){
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followerUser(@PathParam("userId") Long userId, FollowerRequest followerRequest){

        if(userId.equals(followerRequest.getFollowerId())){ //tratando erro de seguir a si mesmo
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(followerRequest.getFollowerId());

        boolean follows = followerRepository.follows(follower, user); //expressão se segue ou não

        if(!follows){ //senão segue adiciona
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> list = followerRepository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size()); //diz quantos vinheram o list.size é a quantidade de followers
        List<FollowerResponse> followerList =  list.stream()
                .map(FollowerResponse::new) //map ira lista de follower para a liista de followerresponse
                //o dois pontos está passando referencia do método passando cada um dos followers
                .collect(Collectors.toList()); //o collect

        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();

    }

    @DELETE
    @Transactional
    public Response unfollowerUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAndUser(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
          //return Response.status(Response.Status.OK).entity(followerId).build(); //para efeitos de teste se captura follower
//        estamos capturando esse followerId pega url http://localhost:8080/users/4/followers?followerId=5

    }
}
