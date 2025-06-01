package sv.edu.udb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.dto.ContenidoRequest;
import sv.edu.udb.model.Contenido;
import sv.edu.udb.service.ContenidoService;
import org.springframework.http.HttpStatus;


import java.util.List;

@RestController
@RequestMapping("/api/contenido")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    // Listar contenido (usuarios con rol USER o ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")

    public ResponseEntity<List<Contenido>> getAll(@RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(contenidoService.getAll(tipo));
    }

    // Crear contenido (usuarios con rol USER o ADMIN)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")

    public ResponseEntity<Contenido> createContenido(@RequestBody ContenidoRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        Contenido contenido = contenidoService.createContenido(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(contenido);
    }

}