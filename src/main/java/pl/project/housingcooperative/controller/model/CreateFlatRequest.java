package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateFlatRequest {
    private String address;
    private Integer area;


}
