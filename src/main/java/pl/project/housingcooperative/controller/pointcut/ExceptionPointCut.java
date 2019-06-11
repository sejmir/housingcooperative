package pl.project.housingcooperative.controller.pointcut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.digitalvirgo.mqubeha2.exception.ResourceNotFoundException;
import pl.digitalvirgo.mqubeha2.mp.MQubeHA2MpServicePort;
import pl.digitalvirgo.mqubeha2.security.CurrentUserService;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionPointCut {
    private final MQubeHA2MpServicePort mQubeHa2MpServicePort;
    private final CurrentUserService currentUserService;

    @Around("execution(* pl.digitalvirgo.mqubeha2.*.api.controller.*.*(..))")
    public ResponseEntity prepareResponse(ProceedingJoinPoint p) {
        try {
            Thread.currentThread().setName(p.getSignature().getName() + "_" + currentUserService.getCurrentUserName());
            log.info("roles: {}", currentUserService.getRoles());
            return (ResponseEntity) p.proceed();
        } catch (IllegalStateException e) {
            log.error("Illegal state", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Bad request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Throwable e) {
            mQubeHa2MpServicePort.reportError(e.getMessage(), e);
            log.warn("Returning error sender. Cause: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

}
