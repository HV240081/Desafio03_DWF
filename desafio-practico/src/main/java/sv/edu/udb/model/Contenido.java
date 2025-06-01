package sv.edu.udb.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(length = 500)
    private String descripcion;

    @Column
    private Integer cantidadEpisodios;

    @Column
    private Integer duracionPromedio;

    @Column
    private String genero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creadoPor", nullable = false)
    private Usuario creadoPor;

    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
