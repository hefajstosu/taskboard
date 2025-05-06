package io.github.hefajstosu.taskboard.board;

import io.github.hefajstosu.taskboard.security.CurrentUserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    @GetMapping("/api/projects/{projectId}/board") 
    public BoardResponse board(@PathVariable Long projectId, @CurrentUserId Long userId) {
        return boardService.getBoard(projectId, userId);
    }
}
