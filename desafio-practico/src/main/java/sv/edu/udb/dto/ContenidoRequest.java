package sv.edu.udb.dto;

import lombok.Data;

@Data
public class ContenidoRequest {
    private String titulo;
    private String tipo;
    private String descripcion;
    private Integer cantidadEpisodios;
    private Integer duracionPromedio;
    private String genero;

}