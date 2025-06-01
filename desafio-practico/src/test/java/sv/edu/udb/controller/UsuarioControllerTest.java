package sv.edu.udb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.service.UsuarioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCurrentUser_UsuarioExiste() {
        // Arrange
        String username = "testUser";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail("test@example.com");
        usuario.setRole("USER");

        when(userDetails.getUsername()).thenReturn(username);
        when(usuarioService.getByUsername(username)).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = usuarioController.getCurrentUser(userDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("USER", response.getBody().getRole());
    }

    @Test
    void testGetCurrentUser_UsuarioNoExiste() {
        // Arrange
        String username = "unknownUser";
        when(userDetails.getUsername()).thenReturn(username);
        when(usuarioService.getByUsername(username)).thenReturn(null);

        // Act
        ResponseEntity<Usuario> response = usuarioController.getCurrentUser(userDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
