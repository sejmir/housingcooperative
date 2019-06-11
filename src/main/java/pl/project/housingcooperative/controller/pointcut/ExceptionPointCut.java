package pl.project.housingcooperative.controller.pointcut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.project.housingcooperative.exception.ForbiddenException;
import pl.project.housingcooperative.exception.ResourceNotFoundException;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionPointCut {

    @Around("execution(* pl.project.housingcooperative.controller.*.*(..))")
    public ResponseEntity prepareResponse(ProceedingJoinPoint p) {
        try {
            Thread.currentThread().setName(p.getSignature().getName());
            return (ResponseEntity) p.proceed();
        } catch (IllegalStateException e) {
            log.error("Illegal state", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Bad request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (ForbiddenException e) {
            log.error("Forbidden request", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            log.error("not found request", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", e.getMessage()));
        } catch (Throwable e) {
            log.error("Bad request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

}
