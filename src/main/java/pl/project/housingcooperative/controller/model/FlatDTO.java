package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.Flat;

@Getter
@ToString
public class FlatDTO {
    private Long id;
    private String address;
    private Integer area;
    private Long ownerId;

    public FlatDTO(Flat flat) {
        this.id = flat.getId();
        this.address = flat.getAddress();
        this.area = flat.getArea();
        this.ownerId = flat.getOwner().getId();

    }
}
