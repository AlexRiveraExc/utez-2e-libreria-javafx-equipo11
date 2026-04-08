package utez.edu.mx.libreria.model;

/**
 * Modelo que representa un Libro en el catálogo de la biblioteca.
 *
 * @author Integrante 1 (Backend)
 */
public class Libro {

    private String isbn;
    private String titulo;
    private String autor;
    private int anio;
    private String genero;
    private boolean disponible;

    // ── Constructor vacío ──────────────────────────────────────────────
    public Libro() {}

    // ── Constructor completo ───────────────────────────────────────────
    public Libro(String isbn, String titulo, String autor,
                 int anio, String genero, boolean disponible) {
        this.isbn       = isbn;
        this.titulo     = titulo;
        this.autor      = autor;
        this.anio       = anio;
        this.genero     = genero;
        this.disponible = disponible;
    }

    // ── Getters y Setters ──────────────────────────────────────────────
    public String getIsbn()            { return isbn; }
    public void   setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo()              { return titulo; }
    public void   setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor()             { return autor; }
    public void   setAutor(String autor) { this.autor = autor; }

    public int  getAnio()          { return anio; }
    public void setAnio(int anio)  { this.anio = anio; }

    public String getGenero()              { return genero; }
    public void   setGenero(String genero) { this.genero = genero; }

    public boolean isDisponible()                { return disponible; }
    public void    setDisponible(boolean disp)   { this.disponible = disp; }

    /**
     * Convierte el objeto a una línea CSV con separador ';'.
     * Formato: isbn;titulo;autor;anio;genero;disponible(1/0)
     */
    public String toCsvLine() {
        return isbn + ";" + titulo + ";" + autor + ";" +
                anio + ";" + genero + ";" + (disponible ? "1" : "0");
    }

    /**
     * Crea un Libro a partir de una línea CSV.
     *
     * @param linea línea con formato isbn;titulo;autor;anio;genero;disponible
     * @return Libro construido, o null si el formato es incorrecto
     */
    public static Libro fromCsvLine(String linea) {
        if (linea == null || linea.isBlank()) return null;
        String[] p = linea.split(";", -1);
        if (p.length != 6) return null;
        try {
            return new Libro(
                    p[0].trim(),
                    p[1].trim(),
                    p[2].trim(),
                    Integer.parseInt(p[3].trim()),
                    p[4].trim(),
                    p[5].trim().equals("1")
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Libro{isbn='" + isbn + "', titulo='" + titulo +
                "', autor='" + autor + "', anio=" + anio +
                ", genero='" + genero + "', disponible=" + disponible + "}";
    }
}