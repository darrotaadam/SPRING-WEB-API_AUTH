package imt.fisa.auth.controllers;

import imt.fisa.auth.controllers.httpdto.AuthenticationRequest;
import imt.fisa.auth.controllers.httpdto.AuthenticationResponse;
import imt.fisa.auth.controllers.httpdto.AuthorizationResponse;
import imt.fisa.auth.services.authentication.AuthenticationService;
import imt.fisa.auth.services.authorization.AuthorizationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    public AuthController(AuthenticationService authenticationService, AuthorizationService authorizationService){
        System.out.println("[*] Creation de AuthController");
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
    }


    @PostMapping(path="/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate( @RequestBody AuthenticationRequest authRequest ){
        System.out.println("[*] AuthController::authenticate called for user "+authRequest.getIdentifiant());
        String token = authenticationService.getAuthorizationToken(authRequest.getIdentifiant(), authRequest.getPassword());
        return ResponseEntity.ok( new AuthenticationResponse(token));
    }

    // attend un token d'authentification dans le header Authorization en mode Bearer
    // retourne le nom d'utilisateur associé au token
    @PostMapping(path="/authorize")
    public ResponseEntity<AuthorizationResponse> authorize(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        System.out.println(authorization); 

        String token = this.authorizationService.extractToken(authorization);
        String username = this.authorizationService.getUser(token);
        return ResponseEntity.ok( new AuthorizationResponse(username));
    }


}
