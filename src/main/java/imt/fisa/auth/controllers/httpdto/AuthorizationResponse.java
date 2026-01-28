package imt.fisa.auth.controllers.httpdto;



public class AuthorizationResponse {
    private String username;
    private String error;

    public AuthorizationResponse(String username){
        this.username = username;
    }
    public AuthorizationResponse(String username, String error){
        this.username = username;
        this.error = error;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
