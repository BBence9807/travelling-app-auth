package hu.bb.travellingappauth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginRequest {

    private String email;
    private String password;
}
