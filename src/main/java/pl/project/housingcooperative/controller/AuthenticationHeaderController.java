package pl.project.housingcooperative.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.project.housingcooperative.configuration.BasicAuthHeaderFilter;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/authenticationHeader")
public class AuthenticationHeaderController {

    @GetMapping
    public ResponseEntity getTenantsByFlatId(@RequestParam String login, String password) {
        String authorization = BasicAuthHeaderFilter.BASIC_PREFIX + new String(Base64.getEncoder().encode((login + ":" + password).getBytes()));
        return ResponseEntity.ok(Map.of(HttpHeaders.AUTHORIZATION, authorization));
    }
}
