package hu.bb.travellingappauth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TwoFactorSetRequest {

    private String email;
    private Boolean value;
}
