package io.github.hefajstosu.taskboard.auth;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc 
class AuthFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void registerLoginAndCallMe() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "Ann@Example.com", "displayName": "Ann", "password": "secret123"}
                                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("ann@example.com"));

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "ann@example.com", "password": "secret123"}
                                """))
                    .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.accessToken");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
            .andExpect(jsonPath("$.displayName").value("Ann"));
    }


    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "bob@example.com", "displayName": "Bob", "password": "secret123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "bob@example.com", "password": "nope-nope"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
