package pl.project.housingcooperative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.project.housingcooperative.controller.model.CreateUserRequest;
import pl.project.housingcooperative.controller.model.UserDTO;
import pl.project.housingcooperative.persistence.model.Flat;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.UserRepository;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/")
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

}
