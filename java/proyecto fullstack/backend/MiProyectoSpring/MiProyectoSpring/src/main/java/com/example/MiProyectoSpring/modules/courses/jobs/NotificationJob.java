package com.example.MiProyectoSpring.modules.courses.services;

import com.example.MiProyectoSpring.models.Course;
import com.example.MiProyectoSpring.modules.courses.dtos.CourseDto;
import com.example.MiProyectoSpring.modules.courses.repositories.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Slf4j
@Component
public class NotificationJob {

    // Constants

    private static final String ERROR_MESSAGE = "Error processing operation settlement notification via bmc job.";

    // Dependencies

    private final OperationRepository operationRepository;
    private final PropertyService propertyService;
    private final WorkingDayService workingDayService;
    private final OperationSettlementBMCNotificationService operationSettlementBMCNotificationService;
    private final OperationSettlementSCBNotificationService operationSettlementSCBNotificationService;

    /**
     * Executes the scheduled job to notification operations for settlement via BMC.
     */
    @Scheduled(fixedDelayString = "${com.ptesa.bmc.core-service.operation.operation-settlement-notification-via-bmc-job-delay-millis}")
    public void execute() {
        try {
            String operationMaxDaysApprovedAllowed = this.propertyService.findDefaultValue(
                    PropertyNames.OPERATION_SETTLEMENT_BILLING_MAX_DAYS_APPROVED_ALLOWED,
                    ERROR_MESSAGE
            );
            String operationNotificationDaysApprovedAllowed = this.propertyService.findDefaultValue(
                    PropertyNames.OPERATION_SETTLEMENT_NOTIFICATION_DAYS_FOR_APPROVED_ALLOWED,
                    ERROR_MESSAGE
            );

            LocalDate dateLimit = this.workingDayService.plusWorkingDays(ZonedDateTime.now().toLocalDate(), (Integer.parseInt(operationMaxDaysApprovedAllowed) + Integer.parseInt(operationNotificationDaysApprovedAllowed)));

            List<Operation> operations = this.operationRepository.findOperationSettlementNotificationViaBmc(dateLimit.atStartOfDay(ZoneId.systemDefault()));
            log.info("Executing operation settlement notification via bmc job. {} total records.", operations.size());

            operations.forEach(this::sendNotification);

        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e);
        }

    }

    /**
     * Sends the settlement notification for the given operation.
     *
     * @param operation The operation to send the notification for.
     */
    private void sendNotification(Operation operation) {
        OperationSettlementBMCNotificationService.Request notificationRequestBMC = new OperationSettlementBMCNotificationService.Request();
        notificationRequestBMC.setOperationId(operation.getId());
        notificationRequestBMC.setBrokerId(operation.getInvestorBroker().getId());
        this.operationSettlementBMCNotificationService.generateNotification(notificationRequestBMC);

        OperationSettlementSCBNotificationService.Request notificationRequestSCB = new OperationSettlementSCBNotificationService.Request();
        notificationRequestSCB.setOperationId(operation.getId());
        notificationRequestSCB.setBrokerId(operation.getInvestorBroker().getId());
        this.operationSettlementSCBNotificationService.generateNotification(notificationRequestSCB);

        // Update status
        this.operationRepository.updateSettlementNotificationStatus(operation.getId());
    }
}
