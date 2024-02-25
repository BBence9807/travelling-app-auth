package hu.bb.travellingappauth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JwtClaim {

    Long id;
    String email;
    Boolean twoFactor;
}
