package pl.project.housingcooperative.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.project.housingcooperative.controller.model.*;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.FlatForLease;
import pl.project.housingcooperative.persistence.model.FlatForSale;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(value = "/currentUser", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class CurrentUserController {
    private final FlatRepository flatRepository;
    private final LeaseRepository leaseRepository;
    private final SaleRepository saleRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public CurrentUserController(FlatRepository flatRepository, LeaseRepository leaseRepository, SaleRepository saleRepository, TenantRepository tenantRepository, UserRepository userRepository) {
        this.flatRepository = flatRepository;
        this.leaseRepository = leaseRepository;
        this.saleRepository = saleRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal User authenitcated) {
        return ResponseEntity.ok(new UserDTO(authenitcated));
    }






}
