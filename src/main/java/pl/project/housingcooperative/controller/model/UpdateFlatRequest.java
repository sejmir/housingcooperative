package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@ToString
public class UpdateFlatForLeaseRequest {
    @Positive
    private int monthPrice;
    @NotBlank
    private String description;
}
