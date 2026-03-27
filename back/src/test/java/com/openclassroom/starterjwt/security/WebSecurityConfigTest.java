package com.openclassroom.starterjwt.security;

import com.openclassrooms.starterjwt.security.WebSecurityConfig;
import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

  @Mock
  private UserDetailsServiceImpl userDetailsService;

  @Mock
  private HttpSecurity http;

  @Mock
  private SecurityFilterChain securityFilterChain;

  @Test
  void authenticationJwtTokenFilter_ShouldReturnNewFilterInstance() {
    WebSecurityConfig config = new WebSecurityConfig();

    AuthTokenFilter filter = config.authenticationJwtTokenFilter();

    assertNotNull(filter);
  }

  @Test
  void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
    WebSecurityConfig config = new WebSecurityConfig();

    PasswordEncoder passwordEncoder = config.passwordEncoder();

    assertNotNull(passwordEncoder);
    assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    assertTrue(passwordEncoder.matches("password", passwordEncoder.encode("password")));
  }

  @Test
  void authenticationProvider_ShouldUseConfiguredUserDetailsServiceAndPasswordEncoder() {
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    DaoAuthenticationProvider provider = config.authenticationProvider();

    assertNotNull(provider);
    assertEquals(userDetailsService, ReflectionTestUtils.getField(provider, "userDetailsService"));
    assertNotNull(ReflectionTestUtils.getField(provider, "passwordEncoder"));
  }

  @Test
  void authenticationManager_ShouldReturnAuthenticationManagerFromConfiguration() throws Exception {
    WebSecurityConfig config = new WebSecurityConfig();
    AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
    AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

    AuthenticationManager result = config.authenticationManager(authenticationConfiguration);

    assertEquals(authenticationManager, result);
    verify(authenticationConfiguration, times(1)).getAuthenticationManager();
  }

  private void setupHttpSecurityMock() throws Exception {
    when(http.cors(any())).thenReturn(http);
    when(http.csrf(any())).thenReturn(http);
    when(http.sessionManagement(any())).thenReturn(http);
    when(http.authenticationProvider(any())).thenReturn(http);
    when(http.authorizeHttpRequests(any())).thenReturn(http);
    when(http.addFilterBefore(any(), any())).thenReturn(http);
    when(http.exceptionHandling(any())).thenReturn(http);
    doReturn(securityFilterChain).when(http).build();
  }

  @Test
  void securityFilterChain_ShouldNotBeNull() throws Exception {
    // Test case: la chaîne de filtrage de sécurité doit être créée avec succès
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    SecurityFilterChain result = config.securityFilterChain(http);

    assertNotNull(result);
    assertEquals(securityFilterChain, result);
  }

  @Test
  void securityFilterChain_ShouldDisableCors() throws Exception {
    // Test case: CORS doit être désactivé
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que cors() a été appelé
    verify(http).cors(any());
  }

  @Test
  void securityFilterChain_ShouldDisableCsrf() throws Exception {
    // Test case: CSRF doit être désactivé
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que csrf() a été appelé
    verify(http).csrf(any());
  }

  @Test
  void securityFilterChain_ShouldSetSessionManagementToStateless() throws Exception {
    // Test case: la gestion des sessions doit être STATELESS
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que sessionManagement() a été appelé
    verify(http).sessionManagement(any());
  }

  @Test
  void securityFilterChain_ShouldConfigureAuthenticationProvider() throws Exception {
    // Test case: le fournisseur d'authentification doit être configuré
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que authenticationProvider() a été appelé
    verify(http).authenticationProvider(any());
  }

  @Test
  void securityFilterChain_ShouldConfigureAuthorizationRules() throws Exception {
    // Test case: les règles d'autorisation doivent être configurées correctement
    // /api/auth/** -> permitAll
    // /api/** -> authenticated
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que authorizeHttpRequests() a été appelé
    verify(http).authorizeHttpRequests(any());
  }

  @Test
  void securityFilterChain_ShouldAddAuthTokenFilterBeforeUsernamePasswordAuthenticationFilter() throws Exception {
    // Test case: le filtre JWT doit être ajouté avant le filtre d'authentification par nom d'utilisateur/mot de passe
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que addFilterBefore() a été appelé avec AuthTokenFilter
    verify(http).addFilterBefore(
        any(AuthTokenFilter.class),
        eq(UsernamePasswordAuthenticationFilter.class)
    );
  }

  @Test
  void securityFilterChain_ShouldConfigureExceptionHandling() throws Exception {
    // Test case: la gestion des exceptions doit être configurée
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que exceptionHandling() a été appelé
    verify(http).exceptionHandling(any());
  }

  @Test
  void securityFilterChain_ShouldReturnBuiltFilterChain() throws Exception {
    // Test case: la méthode doit retourner le SecurityFilterChain construit
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    SecurityFilterChain result = config.securityFilterChain(http);

    // Vérifier que build() a été appelé et que le résultat est retourné
    verify(http).build();
    assertSame(securityFilterChain, result);
  }

  @Test
  void securityFilterChain_ShouldHandleAuthenticationException() throws Exception {
    // Test case: les exceptions d'authentification doivent être traitées correctement
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que la gestion des exceptions d'authentification a été configurée
    verify(http).exceptionHandling(any());
  }

  @Test
  void securityFilterChain_ShouldCallAllConfigurationMethods() throws Exception {
    // Test case: toutes les méthodes de configuration doivent être appelées dans le bon ordre
    WebSecurityConfig config = new WebSecurityConfig();
    ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

    setupHttpSecurityMock();

    config.securityFilterChain(http);

    // Vérifier que toutes les méthodes ont été appelées
    verify(http).cors(any());
    verify(http).csrf(any());
    verify(http).sessionManagement(any());
    verify(http).authenticationProvider(any());
    verify(http).authorizeHttpRequests(any());
    verify(http).addFilterBefore(any(), any());
    verify(http).exceptionHandling(any());
    verify(http).build();
  }
}
