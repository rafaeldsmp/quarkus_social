package io.github.rafaelpino.quarkussocial.rest;

import io.github.rafaelpino.quarkussocial.domain.model.Follower;
import io.github.rafaelpino.quarkussocial.domain.model.User;
import io.github.rafaelpino.quarkussocial.domain.repository.FollowerRepository;
import io.github.rafaelpino.quarkussocial.domain.repository.UserRepository;
import io.github.rafaelpino.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    private UserRepository userRepository;
    @Inject
    private FollowerRepository followerRepository;
    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //usuario padrao dos testes
        User user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId(); //estou garantindo que a variavel global userId vai ter o id do usuario que foi persistido aqui

        //criando seguidor
        User follower  = new User();
        follower.setAge(30);
        follower.setName("Cricrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        //criando um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when follower Id is equal to User id")
    public void sameUserAsFollowerTest(){

        FollowerRequest body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));

    }

    @Test
    @DisplayName("should return 404 on follower a user when user id doesn't exist")
    public void userNotFoundWhenTryingToFollowTest(){

        FollowerRequest body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        FollowerRequest body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and when user id doesn't exist")
    public void userNotFoundWhenListingFollowersTest(){

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                    .pathParam("userId", inexistentUserId)
                .when()
                    .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }



    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id don't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId) //estabelecemos que seria via query param o id do seguidor
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}