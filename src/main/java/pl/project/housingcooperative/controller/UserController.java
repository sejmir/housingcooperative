package pl.project.housingcooperative.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<UserDTO> getAll(@AuthenticationPrincipal User user) {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public UserDTO create(@RequestBody CreateUserRequest createUserRequest) {
        User u = User.builder()
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .address(createUserRequest.getAddress())
                .phone(createUserRequest.getPhone())
                .mail(createUserRequest.getMail())
                .password(createUserRequest.getPassword())
                .userType(createUserRequest.getUserType())
                .build();

        return new UserDTO(userRepository.save(u));
    }

    @PostMapping("/{id}/flats")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlat(
            @RequestParam long id,
            @RequestBody CreateFlatRequest request,
            @AuthenticationPrincipal User authenitcated) {

         if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                     .of("error", "nie masz uprawnień do zmiany tego zasobu"));
         }
         Optional<User> userOpt = userRepository.findById(id);
         if (userOpt.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                     .of("error", "nie znaleziono użytkownika"));
         }
         Flat flat = Flat.builder()
                .address(request.getAddress())
                .area(request.getArea())
                .owner(userOpt.get())
                .build();
        Flat savedFlat = flatRepository.save(flat);

        return ResponseEntity.ok(new FlatDTO(savedFlat));
    }

    @GetMapping("/{id}/flats")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlats(
            @RequestParam long id,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        List<FlatDTO> flats = flatRepository.findByOwner(authenitcated).stream()
                .map(FlatDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }


    @PostMapping("/{id}/flatsForSale")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlatForSale(@RequestBody @Valid CreateFlatForSaleRequest request, @AuthenticationPrincipal User authenticated,@RequestParam long id) {
        if (!authenticated.hasAuthority("ADMINISTRATOR") && !authenticated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        Optional<Flat> flatOpt = flatRepository.findById(request.getFlatId());

        if (flatOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "nie ma mieszkania z tym flatID"));
        }
        if (!flatOpt.get().hasOwner(authenticated)) {
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

    @PutMapping("/{id}/flatsForSale/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity updateFlatForSale(
            @RequestParam long id,
            @RequestParam long flatId,
            @RequestBody UpdateFlatForSaleRequest request,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        Optional <FlatForSale> flatForSaleOpt = saleRepository.findByFlatOwner(authenitcated).stream()//cosik się tu zjebało bo nie chce edytować
                .filter(f -> f.getFlatId() == flatId)
                .findAny();

        if (flatForSaleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "nie znaleziono"));
        }

        flatForSaleOpt.get().setDescription(request.getDescription());
        flatForSaleOpt.get().setSellPrice(request.getSellPrice());

        return ResponseEntity.ok(new FlatForSaleDTO(saleRepository.save(flatForSaleOpt.get())));
    }

    @GetMapping("/{id}/flatsForSale")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatsForSale(
            @RequestParam long id,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        List<FlatForSaleDTO> flats = saleRepository.findByFlatOwner(authenitcated).stream()
                .map(FlatForSaleDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{id}/flatsForSale/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatForSale(
            @RequestParam long id,
            @RequestParam long flatId,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        Optional <FlatForSaleDTO> flatForSaleOpt = saleRepository.findByFlatOwner(authenitcated).stream()
                .filter(f -> f.getFlatId() == flatId)
                .map(FlatForSaleDTO::new)
                .findAny();

        return flatForSaleOpt.map(flat -> (ResponseEntity)ResponseEntity.ok(flat))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "nie znaleziono")));
    }

    @PostMapping("/{id}/flatsForLease")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity createFlatForLease(@RequestBody @Valid CreateFlatForLeaseRequest request, @AuthenticationPrincipal User authenticated,@RequestParam long id) {

        if (!authenticated.hasAuthority("ADMINISTRATOR") && !authenticated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        Optional<Flat> flatOpt = flatRepository.findById(request.getFlatId());
        if (flatOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "nie ma mieszkania z tym flatID"));
        }
        if (!flatOpt.get().hasOwner(authenticated)) {// dodałem negacje !
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

    @GetMapping("/{id}/flatsForLease")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatsForLease(
            @RequestParam long id,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        List<FlatForLeaseDTO> flats = leaseRepository.findByFlatOwner(authenitcated).stream()
                .map(FlatForLeaseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{id}/flatsForLease/{flatId}")
    @PreAuthorize("hasAuthority('OWNER') OR hasAuthority('ADMINISTRATOR')")
    public ResponseEntity getCurrentUserFlatForLease(
            @RequestParam long id,
            @RequestParam long flatId,
            @AuthenticationPrincipal User authenitcated) {

        if (!authenitcated.hasAuthority("ADMINISTRATOR") && !authenitcated.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map
                    .of("error", "nie masz uprawnień do zmiany tego zasobu"));
        }
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map
                    .of("error", "nie znaleziono użytkownika"));
        }
        Optional <FlatForLeaseDTO> flatForLeaseOpt = leaseRepository.findByFlatOwner(authenitcated).stream()
                .filter(f -> f.getFlatId() == flatId)
                .map(FlatForLeaseDTO::new)
                .findAny();

        return flatForLeaseOpt.map(flat -> (ResponseEntity)ResponseEntity.ok(flat))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "nie znaleziono")));
    }



}
