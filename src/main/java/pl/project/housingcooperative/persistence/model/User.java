package pl.project.housingcooperative.persistence.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "\"User\"")
@Builder
@Getter
@ToString
public class User implements UserDetails {
    @Id
    @Column(name = "id_U")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(name = "Name")
    private String firstName;
    @Column(name = "Surname")
    private String lastName;
    @Column(name = "Address")
    private String address;
    @Column(name = "Phone")
    private String phone;
    @Column(name = "Mail", nullable = false)
    private String mail;
    @Column(name = "Password", nullable = false)
    private String password;
    @Column(name = "User_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Tolerate
    public User() {
        //required by JPA
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    public String getUsername() {
        return this.mail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasAuthority(String authority){
        return userType.name().equals(authority);
    }
}
