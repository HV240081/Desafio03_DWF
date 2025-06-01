package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.dto.ContenidoRequest;
import sv.edu.udb.model.Contenido;
import sv.edu.udb.model.Usuario;
import sv.edu.udb.repository.ContenidoRepository;
import sv.edu.udb.repository.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Contenido> getAll(String tipo) {
        if (tipo != null && !tipo.isEmpty()) {
            return contenidoRepository.findByTipo(tipo);
        }
        return contenidoRepository.findAll();
    }

    public Contenido createContenido(ContenidoRequest request, String username) {
        // Buscar el usuario autenticado por nombre de usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!request.getTipo().equalsIgnoreCase("Pelicula") && !request.getTipo().equalsIgnoreCase("Serie")) {
            throw new IllegalArgumentException("Tipo debe ser 'Pelicula' o 'Serie'");
        }

        // Crear nuevo contenido
        Contenido contenido = new Contenido();
        contenido.setTitulo(request.getTitulo());
        contenido.setTipo(request.getTipo());
        contenido.setDescripcion(request.getDescripcion());
        contenido.setCantidadEpisodios(request.getCantidadEpisodios());
        contenido.setDuracionPromedio(request.getDuracionPromedio());
        contenido.setGenero(request.getGenero());
        contenido.setCreadoPor(usuario);
        return contenidoRepository.save(contenido);
    }

}