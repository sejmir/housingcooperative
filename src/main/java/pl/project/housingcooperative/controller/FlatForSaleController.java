package pl.project.housingcooperative.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.project.housingcooperative.controller.model.CreateFlatForSaleRequest;
import pl.project.housingcooperative.controller.model.FlatForSaleDTO;
import pl.project.housingcooperative.controller.model.TenantDTO;
import pl.project.housingcooperative.controller.model.UserDTO;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.FlatForSale;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.FlatRepository;
import pl.project.housingcooperative.persistence.repository.SaleRepository;
import pl.project.housingcooperative.persistence.repository.TenantRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(value = "/flatForSale", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FlatForSaleController {
    private final FlatRepository flatRepository;
    private final SaleRepository saleRepository;
    private final TenantRepository tenantRepository;

    public FlatForSaleController(FlatRepository flatRepository, SaleRepository saleRepository, TenantRepository tenantRepository) {
        this.flatRepository = flatRepository;
        this.saleRepository = saleRepository;
        this.tenantRepository = tenantRepository;
    }

    @GetMapping
    public ResponseEntity getAll() {
        List<FlatForSaleDTO> flats =  saleRepository.findAll().stream()
                .map(FlatForSaleDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{id}")
    public ResponseEntity getByFlatId(@PathVariable long id) {
        Optional<FlatForSale> flatOpt = saleRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new FlatForSaleDTO(flatOpt.get()));
    }

    @GetMapping("/{id}/owner")
    public ResponseEntity getOwnerByFlatId(@PathVariable long id) {
        Optional<FlatForSale> flatOpt = saleRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserDTO(flatOpt.get().getFlat().getOwner()));
    }

    @GetMapping("/{id}/tenants")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') OR hasAuthority('OWNER')")
    public ResponseEntity getTenantsByFlatId(@AuthenticationPrincipal User authenitcated, @PathVariable long id) {
        Optional<FlatForSale> flatOpt = saleRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FlatForSale flatForSale = flatOpt.get();
        if (authenitcated.hasAuthority("OWNER") && !flatForSale.getFlat().hasOwner(authenitcated)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<TenantDTO> tenants = tenantRepository.findAllByFlat(flatForSale.getFlat()).stream()
                .map(TenantDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }
}
