package com.ctechcompiler.backend.repository;

import com.ctechcompiler.backend.model.CodeSnippet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// We don't need a custom finder method for this new logic,
// as students will fetch ALL snippets. JpaRepository's findAll() is enough.
public interface CodeSnippetRepository extends JpaRepository<CodeSnippet, Long> {
    // The old findByStudentUsername is no longer needed.
}

