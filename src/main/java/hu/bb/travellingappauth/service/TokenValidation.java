package hu.bb.travellingappauth.service;

import hu.bb.travellingappauth.helper.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenValidation {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer";

    public Boolean validate(HttpServletRequest request){

        if(request.getHeaders(AUTHORIZATION).nextElement() == null || !request.getHeaders(AUTHORIZATION).nextElement().startsWith(PREFIX))
            return false;

        return jwtUtil.validate(request.getHeaders(AUTHORIZATION).nextElement().split(" ")[1]);

    }
}
