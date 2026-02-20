package imt.fisa.auth.services.authentication;


import imt.fisa.auth.exception.UnauthorizedException;
import imt.fisa.auth.persistence.dto.UserEntity;
import imt.fisa.auth.persistence.repositories.UserRepository;
import imt.fisa.auth.services.crypto.TokenService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;


    public String getAuthorizationToken(String identifiant, String password){
        //retourne un token d'autorisation qui sera utilisé par les autres APIs.
        //Si un token non expiré associé à l'utilisateur existe en bdd : on refresh ce token et le renvoie
        System.out.println("[*] AuthService::getAuthorizationToken called for user "+identifiant);
        Optional<UserEntity> mayberUser = userRepository.findByIdentifiantAndPassword(identifiant, password);
        if(mayberUser.isEmpty()){
            System.out.println("[!] AuthService::getAuthorizationToken invalid credentials for user "+identifiant);
            throw new UnauthorizedException("Identifiant ou password incorrect.");
        }

        UserEntity user = mayberUser.get();
        String nouveauToken = tokenService.generateToken(identifiant);
        user.setToken(nouveauToken);
        user.setTokenExpirationTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return nouveauToken;

    }








}


