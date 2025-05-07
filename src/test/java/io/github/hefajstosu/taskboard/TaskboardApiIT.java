package io.github.hefajstosu.taskboard;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "spring.flyway.clean-disabled=true")
@Testcontainers(disabledWithoutDocker = true)
class TaskboardApiIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    int port;

    RestClient http;
    @BeforeEach
    void setUp() {
        http = RestClient.builder()
                    .baseUrl("http://localhost:" + port)
            .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                })
                    .build();
    }
    @Test 
    void fullKanbanFlow() {
        String owner = registerAndLogin("owner");
        String member = registerAndLogin("member"); 
        String stranger = registerAndLogin("stranger");
        long projectId = post(owner, "/api/projects", Map.of("name", "Website relaunch")).get("id").asLong();
        long todo = post(owner, "/api/projects/" + projectId + "/columns", Map.of("name", "To do")).get("id").asLong();
        long doing = post(owner, "/api/projects/" + projectId + "/columns",
                Map.of("name", "Doing", "wipLimit", 1)).get("id").asLong();

        long t1 = post(owner, "/api/columns/" + todo + "/tasks", Map.of("title", "Design mockups")).get("id").asLong();
        long t2 = post(owner, "/api/columns/" + todo + "/tasks", Map.of("title", "Setup CI")).get("id").asLong();

        // move
        var moved = http.patch().uri("/api/tasks/{id}/move", t1)
                .header("Authorization", "Bearer " + owner)
                    .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("columnId", doing, "position", 0))
                .retrieve().toEntity(JsonNode.class); 
        assertThat(moved.getStatusCode().value()).isEqualTo(200);
        assertThat(moved.getBody().get("columnId").asLong()).isEqualTo(doing);

        // wip
        var blocked = http.patch().uri("/api/tasks/{id}/move", t2)
                .header("Authorization", "Bearer " + owner)
                    .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("columnId", doing, "position", 0))
                .retrieve().toEntity(JsonNode.class); 
        assertThat(blocked.getStatusCode().value()).isEqualTo(422);
        // board
        JsonNode board = get(owner, "/api/projects/" + projectId + "/board").getBody();
        assertThat(board.get("columns")).hasSize(2);
        assertThat(board.get("columns").get(1).get("tasks").get(0).get("title").asText()).isEqualTo("Design mockups");


		// no access
        assertThat(get(stranger, "/api/projects/" + projectId + "/board").getStatusCode().value()).isEqualTo(404);

        // members
        post(owner, "/api/projects/" + projectId + "/members", Map.of("email", email("member")));
        post(member, "/api/tasks/" + t1 + "/comments", Map.of("body", "Looks good!"));
		assertThat(get(member, "/api/tasks/" + t1 + "/comments").getBody()).hasSize(1);

        var deleteByMember = http.delete().uri("/api/projects/{id}", projectId)
                .header("Authorization", "Bearer " + member)
                    .retrieve().toBodilessEntity();
        assertThat(deleteByMember.getStatusCode().value()).isEqualTo(403); 
    }

    private String registerAndLogin(String name) {
        Map<String, String> credentials = Map.of("email", email(name), "password", "password123");
		http.post().uri("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", email(name), "displayName", name, "password", "password123"))
                    .retrieve().toBodilessEntity();
        JsonNode token = http.post().uri("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(credentials)
            .retrieve().body(JsonNode.class);
        return token.get("accessToken").asText();
    }

    private final String run = UUID.randomUUID().toString().substring(0, 8);

    private String email(String name) {
        return name + "-" + run + "@example.com";
    }

    private JsonNode post(String token, String uri, Object body) {
        var response = http.post().uri(uri)
                .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                .body(body) 
                .retrieve().toEntity(JsonNode.class); 
        assertThat(response.getStatusCode().value()).as("POST %s", uri).isEqualTo(201);
        return response.getBody();
    }

    private org.springframework.http.ResponseEntity<JsonNode> get(String token, String uri) {
        return http.get().uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve().toEntity(JsonNode.class); 
    }
}
