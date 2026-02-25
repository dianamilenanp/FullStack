package com.example.MiProyectoSpring.modules.courses.repositories;

import com.example.MiProyectoSpring.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /*
    @Query(value =
            """
                SELECT C
                FROM Course c
                WHERE c.id = :id --parametro
                and c.student = :#{#course.idStudent}--entidad
            """
    )

     */
}
