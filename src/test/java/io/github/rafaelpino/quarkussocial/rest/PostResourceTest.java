package io.github.rafaelpino.quarkussocial.rest;

import io.github.rafaelpino.quarkussocial.domain.model.Follower;
import io.github.rafaelpino.quarkussocial.domain.model.Post;
import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.github.rafaelpino.quarkussocial.domain.repository.FollowerRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.PostRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.UserRepository;
import io.github.rafaelpino.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) //essa anotation coloca ela e define qual o recurso que irá ser testado e nem precisa definir a URL
class PostResourceTest {

    @Inject
    private UserRepository userRepository;
    @Inject
    private FollowerRepository followerRepository;
    @Inject
    private PostRepository postRepository;
    Long userId; //modo para garantir que o usuario vai ter o ID 1
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP(){  //método que vai ser executado antes de cada um do teste que colocar nessa classe
        //usuario padrao dos testes
        User user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId(); //estou garantindo que a variavel global userId vai ter o id do usuario que foi persistido aqui

        //usuario que não segue ninguém
        User userNotFollower = new User();
        user.setAge(33);
        user.setName("cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuario seguidor
        User userFollower = new User();
        user.setAge(33);
        user.setName("beltrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        //criando registro na tabela de follower
        Follower follower = new Follower();
        follower.setUser(user); //para usuario que vai ser seguido
        follower.setFollower(userFollower); //o seguidor dele vai ser o userFollower;
        followerRepository.persist(follower);

        //criando postagem
        Post post = new Post();
        post.setText("Hello!");
        post.setUser(user);
        postRepository.persist(post);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .post()//nao preciso passar url pois está usando o testhttp endpoing ele já infere a url
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentuserId = 999;  //user que não irá ter na base de dados
        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", inexistentuserId)
        .when()
            .post()//nao preciso passar url pois está usando o testhttp endpoing ele já infere a url
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
            var inexistentUserId= 999;

            given()
                    .pathParam("userId", inexistentUserId)
            .when()
                    .get()
            .then()
                    .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given()  //tem que passar o header
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header follower id"));
    }

    @Test
    @DisplayName("should return 400 when followerId doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 999;
        given()  //tem que passar o header
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent follower id"));
    }


    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollower(){
        given()  //tem que passar o header
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return posts")
    public void listPostsTest(){
        given()  //tem que passar o header
            .pathParam("userId", userId)
            .header("followerId", userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1)); //vai deixar 0 para testar pois não criou cenário para fazer postagem e se vir 0 quer dizer que é ok mesmo não listando postagens
    }
}