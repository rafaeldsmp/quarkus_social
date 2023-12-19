package io.github.rafaelpino.quarkussocial.rest; //precisa criar o mesmo pacote que está lá

import io.github.rafaelpino.quarkussocial.rest.dto.CreateUserRequest;
import io.github.rafaelpino.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest //anotation para subir o contexto da alicação do quarkus
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {
    @TestHTTPResource("/users")
    URL apiURL;
    //fazer o teste dos dois aspectos da branch do retorno de erro e o retorno de sucesso, um teste para cada uma das branchs
    @Test
    @DisplayName("should create user an user sucessfully")//especifica o que vai fazer
    @Order(1)
    public void createUserTest(){
        CreateUserRequest user = new CreateUserRequest(); //vai criar o cenário
        user.setName("fulano");
        user.setAge(30);

        Response response =
        given() ///monta a requisição. dado
                .contentType(ContentType.JSON)//o conteudo json
                .body(user) //o corpo da requisição vai ser o usuario que vai ser transformado em json
        .when() //parte da execução
                .post("/users")//recebe a url para qual vai fazer o post/ quando eu fizer esse post
        .then()  //vai fazer a verificação final /então
                .extract().response(); //vai extrair a resposta dessa requisição

        assertEquals(201, response.statusCode());//eu espero que quando fizer a requisição no corpo da requisição eu recebo o código 201
        assertNotNull(response.jsonPath().getString("id")); //quero que não esteja nula, a propriedade que eu quero que não venha nulo é o id
    }

    //incrementar quando não passa os dados válidos para criação do usuário
    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users").then().extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode()); //resposta seja 422
        assertEquals("validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
       // assertEquals("Age is Required", errors.get(0).get("message"));
        //assertEquals("name is required", errors.get(1).get("message"));

    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest(){
        given()
                .contentType(ContentType.JSON)
                .when()
                    .get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));  //posso verificar por exsa expressão o tamanho da array
                //no banco não está cadastrado nenhum usuário
                //ao colocar 1 ele espera ao menos 1 usuário estar cadastrado

    }
}