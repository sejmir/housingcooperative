package pl.project.housingcooperative.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.project.housingcooperative.controller.model.FlatForLeaseDTO;
import pl.project.housingcooperative.controller.model.TenantDTO;
import pl.project.housingcooperative.controller.model.UserDTO;
import pl.project.housingcooperative.persistence.model.FlatForLease;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.LeaseRepository;
import pl.project.housingcooperative.persistence.repository.TenantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(value = "/flatForLease", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FlatForLeaseController {
    private final LeaseRepository leaseRepository;
    private final TenantRepository tenantRepository;

    public FlatForLeaseController(LeaseRepository leaseRepository, TenantRepository tenantRepository) {
        this.leaseRepository = leaseRepository;
        this.tenantRepository = tenantRepository;
    }

   /* @GetMapping
    public List<FlatForLeaseDTO> getAll() {
        return leaseRepository.findAll().stream()
                .map(FlatForLeaseDTO::new)
                .collect(Collectors.toList());
    }*/
    @GetMapping
    public ResponseEntity getAll(){
        List<FlatForLeaseDTO> leases = leaseRepository.findAll().stream()
                .map(FlatForLeaseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/{id}")
    public ResponseEntity getByFlatId(@PathVariable long id) {
        Optional<FlatForLease> flatOpt = leaseRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new FlatForLeaseDTO(flatOpt.get()));
    }

    @GetMapping("/{id}/owner")
    public ResponseEntity getOwnerByFlatId(@PathVariable long id) {
        Optional<FlatForLease> flatOpt = leaseRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserDTO(flatOpt.get().getFlat().getOwner()));
    }

    @GetMapping("/{id}/tenants")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') OR hasAuthority('OWNER')")
    public ResponseEntity getTenantsByFlatId(@AuthenticationPrincipal User authenitcated, @PathVariable long id) {
        Optional<FlatForLease> flatOpt = leaseRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FlatForLease flatForLease = flatOpt.get();
        if (authenitcated.hasAuthority("OWNER") && !flatForLease.getFlat().hasOwner(authenitcated)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<TenantDTO> tenants = tenantRepository.findAllByFlat(flatForLease.getFlat()).stream()
                .map(TenantDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }
}
