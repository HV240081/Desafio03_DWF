package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.udb.dto.RegisterRequest;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevoUsuario");
        request.setPassword("123456");
        request.setEmail("nuevo@correo.com");

        when(usuarioRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("passwordEncoded");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setUsername(request.getUsername());
        usuarioGuardado.setEmail(request.getEmail());
        usuarioGuardado.setPassword("passwordEncoded");
        usuarioGuardado.setRole("USER");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.registerUser(request);

        assertNotNull(resultado);
        assertEquals(request.getUsername(), resultado.getUsername());
        assertEquals(request.getEmail(), resultado.getEmail());
        assertEquals("USER", resultado.getRole());
        verify(usuarioRepository).findByUsername(request.getUsername());
        verify(usuarioRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("usuarioExistente");

        when(usuarioRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.of(new Usuario()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registerUser(request));

        assertEquals("Nombre de usuario ya registrado", ex.getMessage());
        verify(usuarioRepository).findByUsername(request.getUsername());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registerUser_EmailExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevoUsuario");
        request.setEmail("emailExistente@correo.com");

        when(usuarioRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new Usuario()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registerUser(request));

        assertEquals("Correo electrónico ya registrado", ex.getMessage());
        verify(usuarioRepository).findByUsername(request.getUsername());
        verify(usuarioRepository).findByEmail(request.getEmail());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void getByUsername_UserFound_ReturnsUser() {
        String username = "usuarioTest";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.getByUsername(username);

        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        verify(usuarioRepository).findByUsername(username);
    }

    @Test
    void getByUsername_UserNotFound_ThrowsException() {
        String username = "noExiste";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.getByUsername(username));

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(usuarioRepository).findByUsername(username);
    }
}
