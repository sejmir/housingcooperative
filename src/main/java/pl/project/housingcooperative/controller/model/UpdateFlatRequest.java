package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateFlatRequest {
    private Long ownerId;
    private String address;
    private Integer area;
}
