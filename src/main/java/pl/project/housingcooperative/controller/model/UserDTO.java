package pl.project.housingcooperative.controller.model;

import lombok.Getter;
import lombok.ToString;
import pl.project.housingcooperative.persistence.model.User;
import pl.project.housingcooperative.persistence.model.UserType;

@Getter
@ToString
public class UserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String mail;
    private UserType userType;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.mail = user.getMail();
        this.userType = user.getUserType();
    }
}
