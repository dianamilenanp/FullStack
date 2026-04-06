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

    public ThirdPartyFileResponse findThirdPartyFile(Long thirdPartyId, Long fileTypeId, String username) {

        ThirdParty thirdParty = this.entityExistenceService.findRequired(ThirdParty.class, thirdPartyId, ErrorCode.ENTITY_NOT_FOUND, ERROR_MESSAGE, ControlledException.class);
        ThirdPartyFileType thirdPartyFileType = this.entityExistenceService.findRequired(ThirdPartyFileType.class, fileTypeId, ErrorCode.ENTITY_NOT_FOUND, ERROR_MESSAGE, ControlledException.class);
        ThirdPartyFile thirdPartyFile = this.thirdPartyFileRepository.findByThirdPartyAndType(thirdParty, thirdPartyFileType)
                .orElseThrow(() -> new ControlledException(ErrorCode.ENTITY_NOT_FOUND, String.format("%s. The file does not exist for the third party with ID: %s and type ID: %s.", ERROR_MESSAGE, thirdPartyId, fileTypeId)));

        try {
            final String base64 = this.filesUtil.readFileAsBase64(Paths.get(thirdPartyFile.getFilepath()));
            return ThirdPartyFileResponse.builder()
                    .base64(base64)
                    .originalFilename(thirdPartyFile.getOriginalFileName())
                    .build();
        } catch (IOException e) {
            throw new UnexpectedException(ErrorCode.CANNOT_READ_FILE_FROM_DISK, e, String.format("%s. An error occurred reading file from disk. Detail: %s.", ERROR_MESSAGE, e.getMessage()));
        }
    }

}
