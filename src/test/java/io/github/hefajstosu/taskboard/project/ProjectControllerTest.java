package io.github.hefajstosu.taskboard.project;

import io.github.hefajstosu.taskboard.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc; 

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    ProjectService projectService;

    @Test
    void returnsProblemDetailWhenProjectMissing() throws Exception {
        when(projectService.findById(42L)).thenThrow(new NotFoundException("Project", 42L));

        mockMvc.perform(get("/api/projects/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Project 42 not found"));
    }

    @Test
    void rejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "  "}
                                """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());
    }
}
