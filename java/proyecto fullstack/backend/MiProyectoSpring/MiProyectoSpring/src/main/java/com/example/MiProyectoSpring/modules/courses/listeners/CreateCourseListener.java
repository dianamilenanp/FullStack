package com.ptesa.fac.coreservice.modules.thirdparty.services;

import com.ptesa.fac.coreservice.exception.ControlledException;
import com.ptesa.fac.coreservice.models.IdentificationType;
import com.ptesa.fac.coreservice.models.UserAccount;
import com.ptesa.fac.coreservice.modules.thirdparty.dtos.create.CreateThirdPartyRequest;
import com.ptesa.fac.coreservice.modules.thirdparty.dtos.find.ThirdPartyExistResponse;
import com.ptesa.fac.coreservice.modules.thirdparty.models.ThirdParty;
import com.ptesa.fac.coreservice.modules.thirdparty.repositories.ThirdPartyRepository;
import com.ptesa.fac.coreservice.services.EntityExistenceService;
import com.ptesa.fac.coreservice.services.UserAccountService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.ptesa.fac.coreservice.exception.ErrorCode;
import java.util.Optional;
import java.util.Set;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;

/**
 * Service to validate course.
 *
 */
@AllArgsConstructor
@Service
public class CreateCourseListener {


    // Dependencies
    private final StudentRepository studentRepository;

    // Constants
    private static final String ERROR_MESSAGE = "Cannot create a student.";


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Order(1)
    public void execute(CourseProcessedEvent event) {

        Course xourse = event.getCourse();

        StudentData studentData = StudentData.builder()
                .identificationType(billingDocument.getIssuerIdentificationType())
                .identification(billingDocument.getIssuerIdentification())
                .build();

        createStudentIfNotExists(billingDocument, studentData);
    }

    /**
     * Creates a student if it does not already exist.
     */
    private void createStudentIfNotExists(BillingDocument billingDocument, StudentData data) {

        // Check if student already exists
        ThirdPartyExistResponse existResponse = this.validateThirdPartyService.thirdPartyExists(
                billingDocument.getCreationUserAccount().getUsername(),
                data.identificationType.getId(),
                data.identification
        );

        // If the student does not exist, create it
        if (!existResponse.isExists()) {

            this.studentRepository.save(ThirdParty.builder()
                    .factor(billingDocument.getFactor())
                    .identificationType(data.identificationType)
                    .identification(data.identification.trim())
                    .status(ThirdParty.Status.PENDING)
                    .creationBillingDocument(billingDocument)
                    .build());
        }
    }

    /**
     * Represents the data required to create a student.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    private static class StudentData {
        private String identificationType;
        private String identification;
    }
}
