package pl.project.housingcooperative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.project.housingcooperative.configuration.BasicAuthHeaderFilter;
import pl.project.housingcooperative.controller.model.LoginRequest;
import pl.project.housingcooperative.controller.model.LoginResponse;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.repository.UserRepository;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByMailAndPassword(loginRequest.getLogin(), loginRequest.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Brak użytkwonika z tym liginem i/lub hasłem"));
        }
        String authorization = BasicAuthHeaderFilter.BASIC_PREFIX + new String(Base64.getEncoder().encode((loginRequest.getLogin() + ":" + loginRequest.getPassword()).getBytes()));
        return ResponseEntity.ok(new LoginResponse(userOpt.get(), authorization));
    }
}
