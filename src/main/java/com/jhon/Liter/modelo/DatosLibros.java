package com.jhon.Liter.modelo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibros(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DatosAutor> autores,  // Usa DatosAutor (que ya tienes)
        @JsonAlias("languages") List<String> idiomas,    // Ahora es List<String>
        @JsonAlias("download_count") Long numDescargas
) {}