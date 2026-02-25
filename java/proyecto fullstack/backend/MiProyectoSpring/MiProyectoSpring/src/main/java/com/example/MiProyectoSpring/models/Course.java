package com.example.MiProyectoSpring.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    private String category;

    private Boolean active;

    // Execute before persisting the course to db

    @PrePersist
    private void prePersist() {
        this.active = true;
    }

}
