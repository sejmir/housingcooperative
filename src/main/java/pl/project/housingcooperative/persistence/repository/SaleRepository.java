package pl.project.housingcooperative.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.FlatForSale;
import pl.project.housingcooperative.persistence.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<FlatForSale,Long> {
    boolean existsByFlat(Flat flat);

    List<FlatForSale> findByFlatOwner(User user);

    Optional<FlatForSale> findByFlatId(long flatId);
}
