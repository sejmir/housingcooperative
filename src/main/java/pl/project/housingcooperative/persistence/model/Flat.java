package pl.project.housingcooperative.persistence.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Table(name = "Flat")
@Builder
@Getter
@ToString
public class Flat {


    @Id
    @Column(name = "id_F")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(name = "Address")
    private String address;
    @Column(name = "Living_area")
    private String area;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Tolerate
    public Flat(){
    }

    public boolean hasOwner(User user) {
        return this.owner.getId().equals(user.getId());
    }
}
