package imt.fisa.auth.controllers.httpdto;



public class AuthorizationResponse {
    private String identifiant;
    private String error;

    public AuthorizationResponse(String username){
        this.identifiant = username;
    }
    public AuthorizationResponse(String identifiant, String error){
        this.identifiant = identifiant;
        this.error = error;
    }


    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
