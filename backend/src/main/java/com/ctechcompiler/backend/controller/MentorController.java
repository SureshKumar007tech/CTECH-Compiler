package com.ctechcompiler.backend.controller;

import com.ctechcompiler.backend.model.CodeSnippet;
import com.ctechcompiler.backend.service.CodeSnippetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final CodeSnippetService codeSnippetService;

    public MentorController(CodeSnippetService codeSnippetService) {
        this.codeSnippetService = codeSnippetService;
    }

    @PostMapping("/assign-snippet")
    // The request body no longer needs studentUsername
    public ResponseEntity<?> assignSnippet(@RequestBody Map<String, String> body, Authentication authentication) {
        String title = body.get("title");
        String snippet = body.get("snippet");
        // We get the mentor's username securely from their authentication token
        String mentorUsername = authentication.getName();

        if (title == null || snippet == null) {
            return ResponseEntity.badRequest().body("Missing fields: title and snippet are required.");
        }

        CodeSnippet cs = codeSnippetService.assignSnippet(mentorUsername, title, snippet);
        return ResponseEntity.ok(cs);
    }
    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }
}
