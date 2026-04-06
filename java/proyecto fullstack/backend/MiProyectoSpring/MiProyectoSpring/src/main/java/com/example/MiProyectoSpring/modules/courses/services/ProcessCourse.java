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
public class ProcessCourse {

    // Dependencies

    private ApplicationEventPublisher eventPublisher;

    this.eventPublisher.publishEvent(new BillingDocumentProcessedEvent(this, billingDocument));
}
