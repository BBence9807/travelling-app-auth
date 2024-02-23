package hu.bb.travellingappauth.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecoveryCodeCheckRequest {

    private String email;
    private String code;
}
