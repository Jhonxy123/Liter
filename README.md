# 📚 Liter - Gestor de Libros y Autores

**Liter** es una aplicación de consola desarrollada con **Spring Boot** que permite registrar, consultar y listar libros y autores extraídos de una fuente externa. Está pensada como una herramienta educativa para practicar acceso a datos con JPA/Hibernate y modelado relacional con Java.

---

## 🚀 Tecnologías utilizadas

- ✅ Java 17+
- ✅ Spring Boot 3.5.x
- ✅ Spring Data JPA
- ✅ Hibernate
- ✅ H2 Database (o cualquier otro soporte JPA)
- ✅ Maven

---

## 🧠 Funcionalidades

El programa permite realizar las siguientes acciones desde un menú interactivo:

1. 🔍 Buscar libro por título
2. 📚 Listar todos los libros registrados
3. 👤 Listar todos los autores registrados
4. ⏳ Listar autores vivos en un determinado año
5. 🌍 Listar libros por idioma
0. ❌ Salir del programa

---

## 🗃️ Estructura del proyecto

src/
├── main/
│ ├── java/
│ │ └── com.jhon.Liter/
│ │ ├── modelo/ # Entidades: Libro, Autor
│ │ ├── repository/ # Interfaces JPA: LibroRepository, AutorRepository
│ │ ├── principal/ # Clase Principal con menú de usuario
│ │ └── LiterApplication # Clase main que arranca Spring Boot
│ └── resources/
│ └── application.properties


---

## 📂 Ejemplo de salida

📚 Libros registrados:
--------------LIBRO--------------------------
Título: Pride and Prejudice
Autor: Austen, Jane
Idioma: en
Descargas: 56919
Elija la opción a través de su número:
1 - Buscar libro por título
2 - Listar libros registrados
3 - Listar autores registrados
4 - Listar autores vivos en un determinado año
5 - Listar libros por idioma
0 - Salir
