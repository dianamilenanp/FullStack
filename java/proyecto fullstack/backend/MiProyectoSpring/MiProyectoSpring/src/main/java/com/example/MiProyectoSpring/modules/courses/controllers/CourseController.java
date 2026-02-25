package com.example.MiProyectoSpring.modules.courses.controllers;

import com.example.MiProyectoSpring.modules.courses.dtos.CourseDto;
import com.example.MiProyectoSpring.modules.courses.services.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

    // Dependencies
    private final CourseService courseService;

    /**
    + Find the courses
    */
    @GetMapping
    public List<CourseDto> findAll() {
        return this.courseService.findAll();
    }

    /**
     * Create a course
     * @param request course dto
     */
    @PostMapping
    public void create(@RequestBody CourseDto request) {
        this.courseService.createCourse(request);
    }

    /**
     * Update a course
     * @param request course dto
     */
    @PutMapping
    public void update(@RequestBody CourseDto request) {
        this.courseService.updateCourse(request);
    }

    /**
     * Delete a course
     * @param id of course
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.courseService.deleteCourse(id);
    }
}
