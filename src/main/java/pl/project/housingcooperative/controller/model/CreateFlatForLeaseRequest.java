package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@ToString
@Validated
public class CreateFlatForLeaseRequest {
    @Positive
    private long flatId;
    @Positive
    private int monthPrice;
    @NotBlank
    private String description;
}
