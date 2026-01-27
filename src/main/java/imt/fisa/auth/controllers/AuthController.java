package imt.fisa.auth.controllers;

import imt.fisa.auth.persistence.dto.AuthenticationRequest;
import imt.fisa.auth.persistence.dto.AuthenticationResponse;
import imt.fisa.auth.services.authentication.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }


    @PostMapping(path="/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate( @RequestBody AuthenticationRequest authRequest ){

        String token = authService.getAuthorizationToken(authRequest.getIdentifiant(), authRequest.getPassword());
        return ResponseEntity.ok( new AuthenticationResponse(token));

    }

    // attend un token d'authentification dans le header Authorization en mode Bearer
    // retourne le nom d'utilisateur associé au token
    /*
    public ResponseBody authorize(){
    }
    */

}
