package pl.project.housingcooperative.persistence.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Table(name = "Tenant_list")
@Builder
@Getter
@ToString
public class Tenant {
    @Id
    @Column(name = "id_T")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "Name")
    private String firstName;
    @Column(name = "Surname")
    private String lastName;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Mail")
    private String mail;
    @ManyToOne
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @Tolerate
    public Tenant(){
    }

}
