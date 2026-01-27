package imt.fisa.auth.services.authentication;


import imt.fisa.auth.persistence.dto.UserEntity;
import imt.fisa.auth.persistence.repositories.UserRepository;
import imt.fisa.auth.services.crypto.TokenService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;
    private TokenService tokenService;


    public String getAuthorizationToken(String identifiant, String password){
        //retourne un token d'autorisation qui sera utilisé par les autres APIs.
        //Si un token non expiré associé à l'utilisateur existe en bdd : on refresh ce token et le renvoie

        Optional<UserEntity> userEntiy = userRepository.findByIdentifiantAndPassword(identifiant, password);
        if(userEntiy.isEmpty()){
            throw new RuntimeException("Identifiant ou password incorrect.");
        }

        String nouveauToken = generateToken(identifiant);
        userEntiy.get().setToken(nouveauToken);
        userRepository.save(userEntiy.get());
        return nouveauToken;

        /*
        //verifie si un token existe pour l'utilisateur
        String token = userEntiy.get().getToken();
        if(token == null || token.isEmpty() || !isTokenValid(token)){
            //on génère un nouveau token

            String nouveauToken = generateToken(identifiant);
            userEntiy.get().setToken(nouveauToken);
            userRepository.save(userEntiy.get());
            return nouveauToken;
        } else {
            //si l'utilisateur a déjà un token valide.
            // on lui remet 1h


        }
        */

    }



    private String generateToken(String identifiant) {
        String payload = identifiant + "-" + LocalDateTime.now();
        return tokenService.encrypt(payload);
    }


    private boolean isTokenValid(String token) {
        String[] parts = getTokenParts(token);

        String username = parts[0];
        LocalDateTime issuedAt = LocalDateTime.parse(parts[1]);
        return issuedAt.plusHours(1).isAfter(LocalDateTime.now());
    }


    private String[] getTokenParts (String token){
        String decrypted = tokenService.decrypt(token);
        return decrypted.split("\\-");

    }




}


