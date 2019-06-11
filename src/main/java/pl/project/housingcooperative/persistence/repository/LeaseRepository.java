package pl.project.housingcooperative.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.FlatForLease;
import pl.project.housingcooperative.persistence.model.User;

import java.util.Optional;

@Repository
public interface LeaseRepository extends JpaRepository<FlatForLease,Long> {


    boolean existsByFlat(Flat flat);

    Optional<FlatForLease> findByFlatOwner(User owner);
}
