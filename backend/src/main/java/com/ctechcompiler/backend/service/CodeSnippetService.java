package com.ctechcompiler.backend.service;

import com.ctechcompiler.backend.model.CodeSnippet;
import com.ctechcompiler.backend.repository.CodeSnippetRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CodeSnippetService {

    private final CodeSnippetRepository repository;

    public CodeSnippetService(CodeSnippetRepository repository) {
        this.repository = repository;
    }

    // The method no longer needs studentUsername
    public CodeSnippet assignSnippet(String mentorUsername, String title, String snippet) {
        CodeSnippet cs = new CodeSnippet();
        cs.setMentorUsername(mentorUsername); // We now store the mentor's username
        cs.setTitle(title);
        cs.setSnippet(snippet);
        return repository.save(cs);
    }

    // This is the new method for students to get ALL available snippets
    public List<CodeSnippet> getAllSnippets() {
        return repository.findAll();
    }
}
