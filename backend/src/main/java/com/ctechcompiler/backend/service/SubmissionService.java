package com.ctechcompiler.backend.service;

import com.ctechcompiler.backend.model.Submission;
import com.ctechcompiler.backend.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionRepository repo;

    public SubmissionService(SubmissionRepository repo) {
        this.repo = repo;
    }

    public Submission submitCode(String studentUsername, String snippetTitle, String submittedCode) {
        Submission s = new Submission();
        s.setStudentUsername(studentUsername);
        s.setSnippetTitle(snippetTitle);
        s.setSubmittedCode(submittedCode);

        // Simple “evaluation”: mark as correct if contains keyword 'main'
        s.setCorrect(submittedCode != null && submittedCode.contains("main"));

        return repo.save(s);
    }

    public List<Submission> getSubmissionsForStudent(String username) {
        return repo.findByStudentUsername(username);
    }
    public List<Submission> getAllSubmissions() {
        return repo.findAll();
    }
}
