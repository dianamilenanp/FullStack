package com.example.MiProyectoSpring.modules.courses.services;

import com.example.MiProyectoSpring.models.Course;
import com.example.MiProyectoSpring.modules.courses.dtos.CourseDto;
import com.example.MiProyectoSpring.modules.courses.repositories.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CourseService {

    //Dependencies
    private final CourseRepository courseRepository;

    /**
     * Find the courses
     * @return dto list
     */
    public List<CourseDto> findAll() {
        List<Course> courses = this.courseRepository.findAll();
        //Convert to dto
        return courses.stream()
                .map(this::mapCourseToDto)
                .toList();
    }

    /**
     * Create a course
     */
    public void createCourse(CourseDto request) {
        // Map the request to a course entity
        Course course = this.mapCourseToEntity(request);
        this.courseRepository.save(course);
    }

    /**
     * Update a course
     */
    public void updateCourse(CourseDto request) {
        // Find id
        Course course = courseRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getId()));

        course.setName(request.getName());
        this.courseRepository.save(course);
    }

    /**
     * Delete a course
     */
    public void deleteCourse(Long id) {
        // Find id
        Optional<Course> courseOptional = this.courseRepository.findById(id);

        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            this.courseRepository.delete(course);
        }
    }

    /**
     * Map to entity
     */
    private Course mapCourseToEntity(CourseDto request) {
         return Course.builder()
                .name(request.getName())
                .build();
    }

    /**
     * Map to dto
     */
    private CourseDto mapCourseToDto(Course course){
        return CourseDto.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .build();
    }

}
