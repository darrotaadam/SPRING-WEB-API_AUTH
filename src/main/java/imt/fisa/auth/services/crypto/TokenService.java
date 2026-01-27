package imt.fisa.auth.services.crypto;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class TokenService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private final SecretKeySpec key;

    public TokenService(@Value("${auth.token.secret}") String secret){
        this.key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public String encrypt(String data){
        try{
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
        }catch(Exception e){
            throw new RuntimeException("Erreur lors du chiffrement du token d'authorisation.");
        }
    }


    public String decrypt(String token){
        try{
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(token);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("Erreur lors du déchiffrement du token d'authorisation.");
        }
    }


}
