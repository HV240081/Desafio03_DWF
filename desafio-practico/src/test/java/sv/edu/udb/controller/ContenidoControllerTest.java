package sv.edu.udb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import sv.edu.udb.dto.ContenidoRequest;
import sv.edu.udb.model.Contenido;
import sv.edu.udb.service.ContenidoService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContenidoControllerTest {

    @InjectMocks
    private ContenidoController contenidoController;

    @Mock
    private ContenidoService contenidoService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodoSinTipo() {
        // Arrange
        Contenido contenido1 = new Contenido();
        contenido1.setId(1L);
        contenido1.setTitulo("Contenido 1");

        Contenido contenido2 = new Contenido();
        contenido2.setId(2L);
        contenido2.setTitulo("Contenido 2");

        List<Contenido> contenidos = Arrays.asList(contenido1, contenido2);
        when(contenidoService.getAll(null)).thenReturn(contenidos);

        // Act
        ResponseEntity<List<Contenido>> response = contenidoController.getAll(null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Contenido 1", response.getBody().get(0).getTitulo());
    }

    @Test
    void testObtenerTodoConTipo() {
        // Arrange
        String tipo = "video";

        Contenido contenido = new Contenido();
        contenido.setId(3L);
        contenido.setTitulo("Video de prueba");

        when(contenidoService.getAll(tipo)).thenReturn(List.of(contenido));

        // Act
        ResponseEntity<List<Contenido>> response = contenidoController.getAll(tipo);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Video de prueba", response.getBody().get(0).getTitulo());
    }

    @Test
    void testCrearContenido() {
        // Arrange
        ContenidoRequest request = new ContenidoRequest();
        request.setTitulo("Nuevo contenido");

        when(userDetails.getUsername()).thenReturn("admin");

        Contenido contenido = new Contenido();
        contenido.setTitulo("Nuevo contenido");

        when(contenidoService.createContenido(request, "admin")).thenReturn(contenido);

        // Act
        ResponseEntity<Contenido> response = contenidoController.createContenido(request, userDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Nuevo contenido", response.getBody().getTitulo());
    }
}
