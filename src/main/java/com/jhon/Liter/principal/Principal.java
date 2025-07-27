package com.jhon.Liter.principal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhon.Liter.modelo.Autor;
import com.jhon.Liter.modelo.DatosAutor;
import com.jhon.Liter.modelo.DatosLibros;
import com.jhon.Liter.modelo.Libro;
import com.jhon.Liter.repository.AutorRepository;
import com.jhon.Liter.repository.LibroRepository;
import com.jhon.Liter.service.ConsumoApi;
import com.jhon.Liter.service.ConvierteDatos;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final String URL = "https://gutendex.com/books/?search=";
    private final ConvierteDatos conversor = new ConvierteDatos();

    private final LibroRepository libroRepositorio;
    private final AutorRepository autorRepositorio;

    public Principal(LibroRepository libroRepositorio, AutorRepository autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Elija la opción a través de su número:
                    1 -  Buscar libro por título
                    2 -  Listar libros registrados
                    3 -  Listar autores registrados
                    4 -  Listar autores vivos en un determinado año
                    5 -  Listar libros por idioma
                    0 -  Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> bucarLibro();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnAnio();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la aplicación");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private DatosLibros getDatosLibros() {
        System.out.println("Escriba el nombre del libro que desea buscar: ");
        var nombreLibro = teclado.nextLine();
        var urlCompleta = URL + nombreLibro.replace(" ", "+");
        var json = consumoApi.obtenerDatos(urlCompleta);

        if (json == null || json.isEmpty()) {
            System.err.println("La API no devolvió datos");
            return null;
        }

        try {
            @JsonIgnoreProperties(ignoreUnknown = true)
            record RespuestaAPI(List<DatosLibros> results) {}

            RespuestaAPI respuesta = conversor.obtenerDatos(json, RespuestaAPI.class);

            if (respuesta == null || respuesta.results() == null || respuesta.results().isEmpty()) {
                System.out.println("No se encontraron libros con ese título");
                return null;
            }

            return respuesta.results().get(0);
        } catch (Exception e) {
            System.err.println("Error al parsear JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void bucarLibro() {
        DatosLibros datos = getDatosLibros();
        if (datos == null) return;

        Optional<Libro> libroExistente = libroRepositorio.findByTituloContainsIgnoreCase(datos.titulo());

        if (libroExistente.isPresent()) {
            System.out.println("❌ El libro '" + datos.titulo() + "' ya está registrado en la base de datos.");
        } else {
            Libro libro = new Libro(datos);
            guardarAutores(datos.autores(), libro);
            //libroRepositorio.save(libro);
            System.out.println("✅ Libro guardado: " + datos.titulo());
        }
    }

    @Transactional
    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("\n📚 Libros registrados:");
            libros.forEach(libro -> {
                String autorNombre = (libro.getAutor() != null) ? libro.getAutor().getNombre() : "Desconocido";
                System.out.println(
                        "--------------LIBRO--------------------------" + "\n" +
                                "Título: " + libro.getTitulo() + "\n" +
                                "Autor: " + autorNombre + "\n" +
                                "Idioma: " + libro.getIdioma() + "\n" +
                                "Descargas: " + libro.getNum_descargas() + "\n"
                                + "------------------------------------------------"
                );
            });
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepositorio.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("\n👤 Autores registrados:");
            autores.forEach(autor -> System.out.println(
                    "--------------AUTOR--------------------------" + "\n" +
                            "Nombre: " + autor.getNombre() + "\n" +
                            "Nacimiento: " + autor.getFechaNacimiento() + "\n" +
                            "Fallecimiento: " + autor.getFechaMuerte() + "\n" +
                            "Libros: " + autor.getLibros().size() + "\n"
                            + "------------------------------------------------"
            ));
        }
    }

    private void guardarAutores(List<DatosAutor> datosAutores, Libro libro) {
        if (datosAutores == null || datosAutores.isEmpty()) return;

        for (DatosAutor datosAutor : datosAutores) {
            if (datosAutor.nombre() == null || datosAutor.nombre().isBlank()) continue;

            Optional<Autor> autorExistente = autorRepositorio.findByNombre(datosAutor.nombre());
            Autor autor = autorExistente.orElseGet(() -> autorRepositorio.save(new Autor(datosAutor)));

            libro.setAutor(autor);
            autor.getLibros().add(libro);
            autorRepositorio.save(autor);
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.print("Ingrese el año para buscar autores vivos: ");
        int anio = teclado.nextInt();

        List<Autor> autores = autorRepositorio.findAll();
        System.out.println("\n👤 Autores vivos en el año " + anio + ":");
        autores.stream()
                .filter(autor -> {
                    int nacimiento = autor.getFechaNacimiento();
                    Integer muerte = (autor.getFechaMuerte() != null) ? autor.getFechaMuerte() : null;
                    return nacimiento <= anio && (muerte == null || muerte >= anio);
                })
                .forEach(autor -> System.out.println(
                        "Nombre: " + autor.getNombre() + ", Nacimiento: " + autor.getFechaNacimiento()
                                + (autor.getFechaMuerte() != null ? ", Fallecimiento: " + autor.getFechaMuerte() : "")
                ));
    }


    private void listarLibrosPorIdioma() {
        System.out.print("Ingrese el código del idioma (ej: en, es, fr): ");
        String idioma = teclado.nextLine();

        List<Libro> libros = libroRepositorio.findByIdiomaIgnoreCase(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros para el idioma: " + idioma);
        } else {
            System.out.println("\n📚 Libros en idioma '" + idioma + "':");
            libros.forEach(libro -> System.out.println(
                    "--------------LIBRO--------------------------" + "\n" +
                            "Título: " + libro.getTitulo() + "\n" +
                            "Autor: " + libro.getAutor().getNombre() + "\n" +
                            "Idioma: " + libro.getIdioma() + "\n" +
                            "Descargas: " + libro.getNum_descargas() + "\n" +
                            "------------------------------------------------"
            ));
        }
    }

}
