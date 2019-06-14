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
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.FlatRepository;
import pl.project.housingcooperative.persistence.repository.TenantRepository;

import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/flats", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FlatsController {
    private final FlatRepository flatRepository;
    private final TenantRepository tenantRepository;

    public FlatsController(FlatRepository flatRepository, TenantRepository tenantRepository) {
        this.flatRepository = flatRepository;
        this.tenantRepository = tenantRepository;
    }

  /*  @GetMapping
    public List<Flat> getAll() {
        return flatRepository.findAll();
    }*/
    @GetMapping
    public ResponseEntity getAll(){
        List<Flat> flats = flatRepository.findAll();
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{id}")
    public ResponseEntity getTenantsByFlatId(@PathVariable long id) {
        Optional<Flat> flatOpt = flatRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(flatOpt.get());
    }

    @GetMapping("/{id}/tenants")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') OR hasAuthority('OWNER')")
    public ResponseEntity getTenantsByFlatId(@AuthenticationPrincipal User authenitcated, @PathVariable long id) {
        Optional<Flat> flatOpt = flatRepository.findById(id);
        if (flatOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Flat flat = flatOpt.get();
        if (authenitcated.hasAuthority("OWNER") && !flat.hasOwner(authenitcated)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(tenantRepository.findAllByFlat(flat));

    }


}
