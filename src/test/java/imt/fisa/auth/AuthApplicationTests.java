package imt.fisa.auth;

import imt.fisa.auth.exception.InternalServerErrorException;
import imt.fisa.auth.exception.UnauthorizedException;
import imt.fisa.auth.persistence.dto.UserEntity;
import imt.fisa.auth.persistence.repositories.UserRepository;
import imt.fisa.auth.services.authentication.AuthenticationService;
import imt.fisa.auth.services.authorization.AuthorizationService;
import imt.fisa.auth.services.crypto.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApplicationTests {

    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(new byte[16]);

    // =========================================================================
    // TOKEN SERVICE
    // =========================================================================

    private TokenService tokenService;

    @BeforeEach
    void setUpTokenService() {
        tokenService = new TokenService(TEST_SECRET);
    }

    @Test
    void testTokenService_encryptDecryptRoundtrip() {
        String original = "test-payload";
        String encrypted = tokenService.encrypt(original);
        String decrypted = tokenService.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
        assertThat(encrypted).isNotEqualTo(original).as("Le token chiffré ne doit pas être en clair");
    }

    @Test
    void testTokenService_generateToken_nonVide() {
        String token = tokenService.generateToken("alice");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void testTokenService_generateToken_tokensDifferents() throws InterruptedException {
        String token1 = tokenService.generateToken("alice");
        Thread.sleep(10);
        String token2 = tokenService.generateToken("alice");
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void testTokenService_isTokenValid_tokenNonExpire() {
        UserEntity user = new UserEntity();
        user.setTokenExpirationTime(LocalDateTime.now().plusHours(1));
        String token = tokenService.generateToken("alice");
        user.setToken(token);

        assertThat(tokenService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void testTokenService_isTokenValid_tokenExpire() {
        UserEntity user = new UserEntity();
        user.setTokenExpirationTime(LocalDateTime.now().minusSeconds(1));
        String token = tokenService.generateToken("alice");
        user.setToken(token);

        assertThat(tokenService.isTokenValid(token, user)).isFalse();
    }

    // =========================================================================
    // AUTHENTICATION SERVICE
    // =========================================================================

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService mockedTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testRegister_nouvelUtilisateur_sauvegarde() {
        when(userRepository.findByIdentifiant("bob")).thenReturn(Optional.empty());
        authenticationService.register("bob", "password123");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testRegister_identifiantExistant_leveException() {
        when(userRepository.findByIdentifiant("alice")).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> authenticationService.register("alice", "pwd"))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    void testGetAuthorizationToken_credentialsInvalides_leveUnauthorized() {
        when(userRepository.findByIdentifiantAndPassword("alice", "mauvais")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.getAuthorizationToken("alice", "mauvais"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void testGetAuthorizationToken_credentialsValides_retourneToken() {
        UserEntity user = new UserEntity();
        user.setIdentifiant("alice");
        user.setPassword("secret");

        when(userRepository.findByIdentifiantAndPassword("alice", "secret")).thenReturn(Optional.of(user));
        when(mockedTokenService.generateToken("alice")).thenReturn("token-alice-123");

        String token = authenticationService.getAuthorizationToken("alice", "secret");

        assertThat(token).isEqualTo("token-alice-123");
        verify(userRepository, times(1)).save(user);
    }

    // =========================================================================
    // AUTHORIZATION SERVICE
    // =========================================================================

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void testExtractToken_headerValide_retourneToken() {
        String token = authorizationService.extractToken("Bearer mon-super-token");
        assertThat(token).isEqualTo("mon-super-token");
    }

    @Test
    void testExtractToken_headerInvalide_leveException() {
        assertThatThrownBy(() -> authorizationService.extractToken("Basic abc123"))
                .isInstanceOf(UnauthorizedException.class);

        assertThatThrownBy(() -> authorizationService.extractToken(""))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void testGetUser_tokenInconnu_leveUnauthorized() {
        when(userRepository.findByToken("token-inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorizationService.getUser("token-inconnu"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void testGetUser_tokenExpire_leveUnauthorized() {
        UserEntity user = new UserEntity();
        user.setIdentifiant("alice");
        user.setToken("token-expire");
        user.setTokenExpirationTime(LocalDateTime.now().minusSeconds(1));

        when(userRepository.findByToken("token-expire")).thenReturn(Optional.of(user));
        when(mockedTokenService.isTokenValid("token-expire", user)).thenReturn(false);

        assertThatThrownBy(() -> authorizationService.getUser("token-expire"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void testGetUser_tokenValide_retourneIdentifiantEtProlong() {
        UserEntity user = new UserEntity();
        user.setIdentifiant("alice");
        user.setToken("bon-token");
        user.setTokenExpirationTime(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findByToken("bon-token")).thenReturn(Optional.of(user));
        when(mockedTokenService.isTokenValid("bon-token", user)).thenReturn(true);

        String identifiant = authorizationService.getUser("bon-token");

        assertThat(identifiant).isEqualTo("alice");
        verify(userRepository, times(1)).save(user);
        assertThat(user.getTokenExpirationTime()).isAfter(LocalDateTime.now().plusMinutes(55))
                .as("L'expiration doit être prolongée d'1 heure");
    }
}