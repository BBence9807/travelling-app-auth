package hu.bb.travellingappauth.service;

import hu.bb.travellingappauth.helper.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenValidation {

    @Autowired
    private JwtUtil jwtUtil;

    private final String AUTHORIZATION = "Authorization";
    private final String PREFIX = "Bearer";

    public Boolean validate(HttpServletRequest request){

        if(request.getHeaders(this.AUTHORIZATION).nextElement() == null || !request.getHeaders(this.AUTHORIZATION).nextElement().startsWith(this.PREFIX))
            return false;

        return jwtUtil.validate(request.getHeaders(this.AUTHORIZATION).nextElement().split(" ")[1]);

    }
}
