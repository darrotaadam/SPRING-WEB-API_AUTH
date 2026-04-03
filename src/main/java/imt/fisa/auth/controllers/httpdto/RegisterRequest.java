package imt.fisa.auth.controllers.httpdto;

public class RegisterRequest {
    private String identifiant;
    private String password;

    public RegisterRequest(String identifiant, String password) {
        this.identifiant = identifiant;
        this.password = password;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
