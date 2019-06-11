package pl.project.housingcooperative.persistence.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Table(name = "Sell_list")
@Builder
@Getter
@ToString
public class FlatForSale {
    @Id
    @Column(name = "flat_id")
    private long flatId;

    @OneToOne
    @PrimaryKeyJoinColumn(name="flat_id", referencedColumnName="id_F")
    private Flat flat;

    @Column(name = "sell_price")
    @Setter
    private Integer sellPrice;
    @Column(name = "description")
    @Setter
    private String description;

    @Tolerate
    public FlatForSale(){
    }

}
