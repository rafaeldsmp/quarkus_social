package io.github.rafaelpino.quarkussocial.rest;

import io.github.rafaelpino.quarkussocial.domain.model.Post;
import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.github.rafaelpino.quarkussocial.domain.repository.FollowerRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.PostRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.UserRepository;
import io.github.rafaelpino.quarkussocial.rest.dto.CreatePostRequest;
import io.github.rafaelpino.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.bytebuddy.description.annotation.AnnotationDescription;

import java.util.List;
import java.util.stream.Collectors;

@Path("/user/{userId}/posts") //modelo de subrecurso
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @Transactional
    @POST
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){ //pathparam utilizado para definir o parametro que
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status((Response.Status.NOT_FOUND)).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }

    //criando regra de negocio que só pode visualizar as postagems de certo usuario se for seguidor poderia ser por query paramst igual fez em followerresearch delete mas iremos fazer via header
    @GET
    public Response listPost(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId){ ///é um header customizado
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status((Response.Status.NOT_FOUND)).build();
        }

        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header follower id").build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Inexistent follower id").build();

        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }

        PanacheQuery<Post> query =  postRepository.find("user", Sort.by("dateTime", Sort.Direction.Ascending), user); //sortby é organizar o que vai primeiro

        List<Post> list = query.list();

        List<PostResponse>  postResponseList = list.stream()
 //               .map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity)//passando por método de referencia passando só a referencia do método que quero executar
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
}
