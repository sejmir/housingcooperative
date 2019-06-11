package pl.project.housingcooperative.persistence.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Table(name = "Lease_list")
@Builder
@Getter
@ToString
public class FlatForLease {
    @Id
    @Column(name = "flat_id")
    private long flatId;

    @OneToOne
    @PrimaryKeyJoinColumn(name="flat_id", referencedColumnName="id_F")
    private Flat flat;
    @Setter
    @Column(name = "Lease_monthly_fee")
    private Integer month;
    @Column(name = "description")
    @Setter
    private String description;

    @Tolerate
    public FlatForLease(){
    }
}
