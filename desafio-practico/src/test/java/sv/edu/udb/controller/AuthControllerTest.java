package sv.edu.udb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import sv.edu.udb.dto.LoginRequest;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.security.JwtUtils;
import sv.edu.udb.service.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Prueba exitosa login
    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("pass");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("jwt-token");

        // Mockear contexto de seguridad
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtils, times(1)).generateToken(userDetails);
    }

    // Prueba login fallido por credenciales inválidas
    @Test
    void testLogin_Fail_BadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    // Prueba registro exitoso con rol USER por defecto
    @Test
    void testRegister_Success_DefaultRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setEmail("email@example.com");
        request.setRole(null); // No especifica rol, debe asignar USER

        Usuario usuario = new Usuario();
        usuario.setUsername("newuser");

        when(usuarioService.registerUser(any(RegisterRequest.class))).thenReturn(usuario);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuario, response.getBody());

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(usuarioService).registerUser(captor.capture());
        assertEquals("USER", captor.getValue().getRole());
    }

    // Prueba registro fallido con rol inválido
    @Test
    void testRegister_Fail_InvalidRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setEmail("email@example.com");
        request.setRole("INVALID_ROLE");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rol inválido. Solo ADMIN o USER permitidos.", response.getBody());

        verify(usuarioService, never()).registerUser(any());
    }

    // Prueba registro fallido por excepción del servicio
    @Test
    void testRegister_Fail_ServiceException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setEmail("email@example.com");
        request.setRole("ADMIN");

        when(usuarioService.registerUser(any())).thenThrow(new RuntimeException("Error al registrar"));

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error al registrar", response.getBody());
    }
}
