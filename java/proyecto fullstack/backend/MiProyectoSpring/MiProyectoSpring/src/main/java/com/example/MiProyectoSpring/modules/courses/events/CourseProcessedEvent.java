package com.ptesa.fac.coreservice.modules.billingdocument.modules.upload.processor.common.processbillingdocument.events;

import com.ptesa.fac.coreservice.modules.billingdocument.models.BillingDocument;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is fired after a course is created.
 */
@Getter
public class CourseProcessedEvent extends ApplicationEvent {

    // Fields
    private final Course course;


    // Logic

    /**
     * Constructor
     * @param source Object that fired the event.
     * @param course coursethat was created
     */
    public CourseProcessedEvent(Object source, Course course) {
        super(source);
        this.course = course;
    }
}