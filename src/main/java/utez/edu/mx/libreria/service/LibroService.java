package utez.edu.mx.libreria.service;

import utez.edu.mx.libreria.model.Libro;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio con toda la lógica de negocio:
 * CRUD, carga/guardado en CSV y exportación de reporte.
 *
 * @author Integrante 1 (Backend)
 */
public class LibroService {

    private static final String ARCHIVO_DATOS   = "data/catalogo.csv";
    private static final String ARCHIVO_REPORTE = "reporte_catalogo.csv";

    private final List<Libro> catalogo = new ArrayList<>();

    public LibroService() {
        cargarDesdeArchivo();
    }

    // ══════════════════════════════════════════════════════════════════
    //  CRUD
    // ══════════════════════════════════════════════════════════════════

    /** Alta: agrega un libro si el ISBN no existe. */
    public void agregarLibro(Libro libro) {
        validarLibro(libro);
        if (existeIsbn(libro.getIsbn())) {
            throw new IllegalArgumentException(
                    "Ya existe un libro con el ISBN: " + libro.getIsbn());
        }
        catalogo.add(libro);
        guardarEnArchivo();
    }

    /** Consulta: retorna copia de toda la lista. */
    public List<Libro> obtenerTodos() {
        return new ArrayList<>(catalogo);
    }

    /** Busca un libro por ISBN. */
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return catalogo.stream()
                .filter(l -> l.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    /** Edición: actualiza un libro existente por ISBN. */
    public void actualizarLibro(String isbnOriginal, Libro actualizado) {
        validarLibro(actualizado);
        if (!isbnOriginal.equalsIgnoreCase(actualizado.getIsbn())
                && existeIsbn(actualizado.getIsbn())) {
            throw new IllegalArgumentException(
                    "El nuevo ISBN ya está registrado: " + actualizado.getIsbn());
        }
        boolean encontrado = false;
        for (int i = 0; i < catalogo.size(); i++) {
            if (catalogo.get(i).getIsbn().equalsIgnoreCase(isbnOriginal)) {
                catalogo.set(i, actualizado);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            throw new IllegalArgumentException(
                    "No se encontró el libro con ISBN: " + isbnOriginal);
        }
        guardarEnArchivo();
    }

    /** Eliminación: elimina un libro por ISBN. */
    public boolean eliminarLibro(String isbn) {
        boolean ok = catalogo.removeIf(
                l -> l.getIsbn().equalsIgnoreCase(isbn));
        if (ok) guardarEnArchivo();
        return ok;
    }

    // ══════════════════════════════════════════════════════════════════
    //  Persistencia en archivo
    // ══════════════════════════════════════════════════════════════════

    /** Carga el catálogo desde el CSV al arrancar la app. */
    public final void cargarDesdeArchivo() {
        catalogo.clear();
        Path ruta = Path.of(ARCHIVO_DATOS);
        try {
            Files.createDirectories(ruta.getParent());
            if (!Files.exists(ruta)) {
                Files.createFile(ruta);
                return;
            }
            for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
                if (linea.isBlank() || linea.startsWith("#")) continue;
                Libro libro = Libro.fromCsvLine(linea);
                if (libro != null) catalogo.add(libro);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al cargar el catálogo: " + e.getMessage(), e);
        }
    }

    /** Guarda toda la lista en el CSV. Se llama tras cada cambio. */
    public void guardarEnArchivo() {
        Path ruta = Path.of(ARCHIVO_DATOS);
        try {
            Files.createDirectories(ruta.getParent());
            List<String> lineas = new ArrayList<>();
            lineas.add("# Catálogo — " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            for (Libro l : catalogo) lineas.add(l.toCsvLine());
            Files.write(ruta, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al guardar el catálogo: " + e.getMessage(), e);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  Exportar reporte
    // ══════════════════════════════════════════════════════════════════

    /** Genera reporte_catalogo.csv y retorna la ruta absoluta. */
    public String exportarReporte() {
        Path ruta = Path.of(ARCHIVO_REPORTE);
        try {
            List<String> lineas = new ArrayList<>();
            lineas.add("# Reporte del Catálogo de la Biblioteca");
            lineas.add("# Generado: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            lineas.add("# Total de libros: " + catalogo.size());
            lineas.add("");
            lineas.add("ISBN;Título;Autor;Año;Género;Disponible");
            for (Libro l : catalogo) {
                lineas.add(l.getIsbn() + ";" + l.getTitulo() + ";" +
                        l.getAutor() + ";" + l.getAnio() + ";" +
                        l.getGenero() + ";" +
                        (l.isDisponible() ? "Sí" : "No"));
            }
            long disp = catalogo.stream().filter(Libro::isDisponible).count();
            lineas.add("");
            lineas.add("# Disponibles: " + disp +
                    " | Prestados: " + (catalogo.size() - disp));
            Files.write(ruta, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return ruta.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al exportar el reporte: " + e.getMessage(), e);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  Validaciones
    // ══════════════════════════════════════════════════════════════════

    public void validarLibro(Libro libro) {
        if (libro == null)
            throw new IllegalArgumentException("El libro no puede ser nulo.");

        if (libro.getIsbn() == null || libro.getIsbn().isBlank())
            throw new IllegalArgumentException("El ISBN no puede estar vacío.");

        if (libro.getTitulo() == null || libro.getTitulo().isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío.");
        if (libro.getTitulo().trim().length() < 3)
            throw new IllegalArgumentException("El título debe tener al menos 3 caracteres.");

        if (libro.getAutor() == null || libro.getAutor().isBlank())
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        if (libro.getAutor().trim().length() < 3)
            throw new IllegalArgumentException("El autor debe tener al menos 3 caracteres.");

        int anioActual = Year.now().getValue();
        if (libro.getAnio() < 1500 || libro.getAnio() > anioActual)
            throw new IllegalArgumentException(
                    "El año debe estar entre 1500 y " + anioActual + ".");

        if (libro.getGenero() == null || libro.getGenero().isBlank())
            throw new IllegalArgumentException("El género no puede estar vacío.");
    }

    // ── Auxiliar ───────────────────────────────────────────────────────
    private boolean existeIsbn(String isbn) {
        return catalogo.stream()
                .anyMatch(l -> l.getIsbn().equalsIgnoreCase(isbn));
    }
}