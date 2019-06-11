package pl.project.housingcooperative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.project.housingcooperative.controller.model.*;
import pl.project.housingcooperative.exception.ForbiddenException;
import pl.project.housingcooperative.exception.ResourceNotFoundException;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.FlatForLease;
import pl.project.housingcooperative.persistence.model.FlatForSale;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.FlatRepository;
import pl.project.housingcooperative.persistence.repository.LeaseRepository;
import pl.project.housingcooperative.persistence.repository.SaleRepository;
import pl.project.housingcooperative.persistence.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlatRepository flatRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private LeaseRepository leaseRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getAll(@AuthenticationPrincipal User user) {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity create(@RequestBody CreateUserRequest createUserRequest) {
        User u = User.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .address(createUserRequest.getAddress())
                .phone(createUserRequest.getPhone())
                .mail(createUserRequest.getMail())
                .password(createUserRequest.getPassword())
                .userType(createUserRequest.getUserType())
                .build();

        return ResponseEntity.ok(new UserDTO(userRepository.save(u)));
    }

    @PostMapping("/{userId}/flats")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlat(
            @PathVariable long userId,
            @RequestBody CreateFlatRequest request,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Flat flat = Flat.builder()
                .address(request.getAddress())
                .area(request.getArea())
                .owner(user)
                .build();
        Flat savedFlat = flatRepository.save(flat);

        return ResponseEntity.ok(new FlatDTO(savedFlat));
    }

    @GetMapping("/{userId}/flats")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlats(
            @PathVariable long userId,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        List<FlatDTO> flats = flatRepository.findByOwner(user).stream()
                .map(FlatDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @PutMapping("/{userId}/flats/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity updateFlat(
            @PathVariable long userId,
            @PathVariable long flatId,
            @RequestBody UpdateFlatRequest request,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<Flat> flatOpt = flatRepository.findById(flatId).stream()//cosik się tu zjebało bo nie chce edytować
                .filter(f -> f.hasOwner(user))
                .findAny();

        if (flatOpt.isEmpty()) {
            throw new ResourceNotFoundException("nie znaleziono");
        }
        Flat flat = flatOpt.get();
        if (request.getAddress() != null) {
            flat.setAddress(request.getAddress());
        }
        if (request.getArea() != null) {
            flat.setArea(request.getArea());
        }
        if (request.getOwnerId() != null) {
            if (!authenticated.hasAuthority("ADMINISTRATOR")) {
                throw new ForbiddenException("Tylko administrator może zmieniać właściciela mieszkania");
            }
            User newOwner = userRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Nie ma użytkownika z id " + request.getOwnerId()));
            flat.setOwner(newOwner);
        }


        return ResponseEntity.ok(new FlatDTO(flatRepository.save(flat)));
    }

    @PostMapping("/{userId}/flatsForSale")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlatForSale(
            @RequestBody @Valid CreateFlatForSaleRequest request,
            @AuthenticationPrincipal User authenticated,
            @PathVariable long userId) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<Flat> flatOpt = flatRepository.findById(request.getFlatId());

        if (flatOpt.isEmpty()) {
            throw new IllegalArgumentException("nie ma mieszkania z tym flatID");
        }
        if (!flatOpt.get().hasOwner(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "nie masz uprawnień do wykonywania zmian na tym mieszkaniu"));
        }
        if (saleRepository.existsByFlat(flatOpt.get())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "jest już mieszkanie na sprzedaż z tym flatID"));
        }

        FlatForSale flatForSale = FlatForSale.builder()
                .description(request.getDescription())
                .flatId(request.getFlatId())
                .flat(flatOpt.get())
                .sellPrice(request.getSalePrice())
                .build();
        FlatForSale savedFlatForSale = saleRepository.save(flatForSale);
        return ResponseEntity.ok(new FlatForSaleDTO(savedFlatForSale));
    }

    @PutMapping("/{userId}/flatsForSale/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity updateFlatForSale(
            @PathVariable long userId,
            @PathVariable long flatId,
            @RequestBody UpdateFlatForSaleRequest request,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<FlatForSale> flatForSaleOpt = saleRepository.findById(flatId).stream()//cosik się tu zjebało bo nie chce edytować
                .filter(f -> f.getFlat().hasOwner(user))
                .findAny();

        if (flatForSaleOpt.isEmpty()) {
            throw new ResourceNotFoundException("nie znaleziono");
        }
        FlatForSale flatForSale = flatForSaleOpt.get();

        if (request.getDescription() != null) {
            flatForSale.setDescription(request.getDescription());
        }
        if (request.getSellPrice() != null) {
            flatForSale.setSellPrice(request.getSellPrice());
        }
        return ResponseEntity.ok(new FlatForSaleDTO(saleRepository.save(flatForSale)));
    }

    @GetMapping("/{userId}/flatsForSale")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatsForSale(
            @PathVariable long userId,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);

        User user = getRequestedUser(userId);
        List<FlatForSaleDTO> flats = saleRepository.findByFlatOwner(user).stream()
                .map(FlatForSaleDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{userId}/flatsForSale/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatForSale(
            @PathVariable long userId,
            @PathVariable long flatId,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<FlatForSaleDTO> flatForSaleOpt = saleRepository.findById(flatId).stream()
                .filter(f -> f.getFlat().hasOwner(user))
                .map(FlatForSaleDTO::new)
                .findAny();

        return flatForSaleOpt.map(flat -> (ResponseEntity) ResponseEntity.ok(flat))
                .orElseThrow(() -> new ResourceNotFoundException("nie znaleziono"));
    }

    @PostMapping("/{userId}/flatsForLease")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlatForLease(
            @RequestBody @Valid CreateFlatForLeaseRequest request,
            @AuthenticationPrincipal User authenticated,
            @PathVariable long userId) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<Flat> flatOpt = flatRepository.findById(request.getFlatId());
        if (flatOpt.isEmpty()) {
            throw new IllegalArgumentException("nie ma mieszkania z tym flatID");
        }
        if (!flatOpt.get().hasOwner(user)) {// dodałem negacje !
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "nie masz uprawnień do wykonywania zmian na tym mieszkaniu"));
        }
        if (leaseRepository.existsByFlat(flatOpt.get())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "jest już mieszkanie na sprzedaż z tym flatID"));
        }
        FlatForLease flatForLease = FlatForLease.builder()
                .description(request.getDescription())
                .flatId(request.getFlatId())
                .flat(flatOpt.get())
                .month(request.getMonthPrice())
                .build();
        FlatForLease savedFlatForLease = leaseRepository.save(flatForLease);
        return ResponseEntity.ok(new FlatForLeaseDTO(savedFlatForLease));
    }

    @GetMapping("/{userId}/flatsForLease")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatsForLease(
            @PathVariable long userId,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        List<FlatForLeaseDTO> flats = leaseRepository.findByFlatOwner(user).stream()
                .map(FlatForLeaseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{userId}/flatsForLease/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatForLease(
            @PathVariable long userId,
            @PathVariable long flatId,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<FlatForLeaseDTO> flatForLeaseOpt = leaseRepository.findById(flatId).stream()
                .filter(f -> f.getFlat().hasOwner(user))
                .map(FlatForLeaseDTO::new)
                .findAny();

        return flatForLeaseOpt.map(flat -> (ResponseEntity) ResponseEntity.ok(flat))
                .orElseThrow(() -> new ResourceNotFoundException("nie znaleziono"));
    }

    @PutMapping("/{userId}/flatsForLease/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity updateFlatForLease(
            @PathVariable long userId,
            @PathVariable long flatId,
            @RequestBody UpdateFlatForLeaseRequest request,
            @AuthenticationPrincipal User authenticated) {

        checkPrivileges(userId, authenticated);
        User user = getRequestedUser(userId);
        Optional<FlatForLease> flatForLeaseOpt = leaseRepository.findById(flatId).stream()//cosik się tu zjebało bo nie chce edytować
                .filter(f -> f.getFlat().hasOwner(user))
                .findAny();

        if (flatForLeaseOpt.isEmpty()) {
            throw new ResourceNotFoundException("nie znaleziono");
        }
        FlatForLease flatForLease = flatForLeaseOpt.get();

        if (request.getDescription() != null) {
            flatForLease.setDescription(request.getDescription());
        }
        if (request.getMonthPrice() != null) {
            flatForLease.setMonth(request.getMonthPrice());
        }

        return ResponseEntity.ok(new FlatForLeaseDTO(leaseRepository.save(flatForLease)));
    }

    private void checkPrivileges(long userId, @AuthenticationPrincipal User authenticated) {
        if (!authenticated.hasAuthority("ADMINISTRATOR") && !authenticated.getId().equals(userId)) {
            throw new ForbiddenException("nie masz uprawnień do zmiany tego zasobu");
        }
    }

    private User getRequestedUser(long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("nie znaleziono użytkownika");
        }
        return userOpt.get();
    }
}
