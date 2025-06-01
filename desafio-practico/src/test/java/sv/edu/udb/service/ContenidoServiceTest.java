package sv.edu.udb.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.udb.dto.ContenidoRequest;
import sv.edu.udb.model.Contenido;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.ContenidoRepository;
import sv.edu.udb.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContenidoServiceTest {

    @InjectMocks
    private ContenidoService contenidoService;

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll_WithTipo_ReturnsFilteredList() {
        String tipo = "Serie";
        Contenido c1 = new Contenido();
        Contenido c2 = new Contenido();

        when(contenidoRepository.findByTipo(tipo)).thenReturn(List.of(c1, c2));

        List<Contenido> result = contenidoService.getAll(tipo);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contenidoRepository).findByTipo(tipo);
        verify(contenidoRepository, never()).findAll();
    }

    @Test
    void testGetAll_WithoutTipo_ReturnsAll() {
        Contenido c1 = new Contenido();
        Contenido c2 = new Contenido();
        Contenido c3 = new Contenido();

        when(contenidoRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        List<Contenido> result = contenidoService.getAll(null);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(contenidoRepository).findAll();
        verify(contenidoRepository, never()).findByTipo(anyString());
    }

    @Test
    void testCreateContenido_Success() {
        String username = "usuarioTest";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);

        ContenidoRequest request = new ContenidoRequest();
        request.setTitulo("Titulo Prueba");
        request.setTipo("Serie");
        request.setDescripcion("Descripcion");
        request.setCantidadEpisodios(10);
        request.setDuracionPromedio(45);
        request.setGenero("Accion");

        Contenido contenidoGuardado = new Contenido();
        contenidoGuardado.setTitulo(request.getTitulo());
        contenidoGuardado.setTipo(request.getTipo());

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenidoGuardado);

        Contenido resultado = contenidoService.createContenido(request, username);

        assertNotNull(resultado);
        assertEquals(request.getTitulo(), resultado.getTitulo());
        verify(usuarioRepository).findByUsername(username);
        verify(contenidoRepository).save(any(Contenido.class));
    }

    @Test
    void testCreateContenido_UsuarioNoEncontrado_ThrowsException() {
        String username = "noExiste";

        ContenidoRequest request = new ContenidoRequest();

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            contenidoService.createContenido(request, username);
        });

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(usuarioRepository).findByUsername(username);
        verify(contenidoRepository, never()).save(any());
    }
}
