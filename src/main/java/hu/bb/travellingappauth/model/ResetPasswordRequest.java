package hu.bb.travellingappauth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResetPasswordRequest {

    private String email;
    private String password;
}
