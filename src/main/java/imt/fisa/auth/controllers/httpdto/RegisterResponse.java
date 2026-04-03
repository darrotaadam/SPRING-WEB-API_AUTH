package imt.fisa.auth.controllers.httpdto;

public class RegisterResponse {
    private String message;
    private String identifiant;

    public RegisterResponse(String message, String identifiant) {
        this.message = message;
        this.identifiant = identifiant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }
}
