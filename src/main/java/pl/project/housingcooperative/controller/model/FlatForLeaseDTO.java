package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.FlatForLease;
import pl.project.housingcooperative.persistence.model.FlatForSale;

@Getter
@ToString
public class FlatForLeaseDTO {

    private Long id;
    private String address;
    private Integer area;
    private Integer sellPrice;
    private String description;

    public FlatForLeaseDTO(FlatForLease flatForLease) {
        this.id = flatForLease.getFlatId();
        this.address = flatForLease.getFlat().getAddress();
        this.area = flatForLease.getFlat().getArea();
        this.sellPrice = flatForLease.getMonth();
        this.description = flatForLease.getDescription();
    }
}
