package pl.project.housingcooperative.controller.model;

import pl.project.housingcooperative.persistence.model.Tenant;

public class TenantDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String mail;

    public TenantDTO(Tenant tenant){
        this.id = tenant.getId();
        this.firstName = tenant.getFirstName();
        this.lastName = tenant.getLastName();
        this.phone = tenant.getPhone();
        this.mail = tenant.getMail();
    }


}
