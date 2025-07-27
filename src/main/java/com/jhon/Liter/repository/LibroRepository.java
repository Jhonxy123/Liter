package com.jhon.Liter.repository;

import com.jhon.Liter.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long > {
    Optional<Libro> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Libro> findByIdiomaIgnoreCase(String idioma);
}


