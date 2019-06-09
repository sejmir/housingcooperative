package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.UserType;

@Getter
@ToString
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String mail;
    private String password;
    private UserType userType;
}
