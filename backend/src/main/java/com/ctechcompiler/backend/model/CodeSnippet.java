package com.ctechcompiler.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CodeSnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String snippet;

    // This field is now changed to track who created the snippet
    private String mentorUsername;

    // We can keep this if we want to track submissions, but for now, it's less relevant
    // private boolean completed = false;
}
