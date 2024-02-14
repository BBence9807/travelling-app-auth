package hu.bb.travellingappauth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends BaseEntity{

    @Column(unique = true, nullable = false, name = "u_email")
    @Email(message = "Please provide valid email address!")
    private String email;

    @Column(nullable = false, name = "u_first_name")
    private String firstName;

    @Column(nullable = false, name = "u_last_name")
    private String lastName;

    @Column(nullable = false, name = "u_password")
    private String password;

    @Column(name = "u_two_factor",columnDefinition = "boolean default false")
    private Boolean twoFactor;
}
