package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.FlatForSale;

@Getter
@ToString
public class FlatForSaleDTO {

    private Long id;
    private String address;
    private Integer area;
    private Integer monthPrice;
    private String description;

    public FlatForSaleDTO(FlatForSale flatForSale) {
        this.id = flatForSale.getFlatId();
        this.address = flatForSale.getFlat().getAddress();
        this.area = flatForSale.getFlat().getArea();
        this.monthPrice = flatForSale.getSellPrice();
        this.description = flatForSale.getDescription();
    }
}
