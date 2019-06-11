package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.model.UserType;

@Getter
@ToString
public class LoginResponse {
    private UserDTO user;
    private String authorizationHeader;

    public LoginResponse(User user, String authorizationHeader) {
        this.user = new UserDTO(user);
        this.authorizationHeader = authorizationHeader;
    }
}
