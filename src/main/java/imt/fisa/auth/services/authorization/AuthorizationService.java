package imt.fisa.auth.services.authorization;

import imt.fisa.auth.exception.UnauthorizedException;
import imt.fisa.auth.persistence.dto.UserEntity;
import imt.fisa.auth.persistence.repositories.UserRepository;
import imt.fisa.auth.services.crypto.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;


    public String extractToken(String authorizationHeader){
        String[] parts = authorizationHeader.split("\\ ");
        if(parts.length !=2 || !parts[0].equals("Bearer")){
            throw new UnauthorizedException("Bearer token must be provided in Authorization header.");
        }
        return parts[1];
    }


    public String getUser(String token){
        Optional<UserEntity> maybeUser = userRepository.findByToken(token);
        if(maybeUser.isEmpty()){
            throw new UnauthorizedException("Token is unknown.");
        }

        UserEntity user = maybeUser.get();

        if(! tokenService.isTokenValid(token, user)){
            throw new UnauthorizedException("Token is expired. You can request a new one at /authenticate by providing identifiant and password json parameters");
        }

        user.setTokenExpirationTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return user.getIdentifiant();
    }




}
