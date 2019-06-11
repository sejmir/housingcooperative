package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@ToString
@Validated
public class CreateFlatForSaleRequest {
    @Positive
    private long flatId;
    @Positive
    private int salePrice;
    @NotBlank
    private String description;
}
