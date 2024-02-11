package hu.bb.travellingappauth.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
