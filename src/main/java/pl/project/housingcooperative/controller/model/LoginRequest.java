package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.User;

@Getter
@ToString
public class LoginRequest {
    private String login;
    private String password;
}
