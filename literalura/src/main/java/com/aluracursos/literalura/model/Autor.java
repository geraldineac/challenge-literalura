package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nombreAutor;
    private Integer anioNacimiento;
    private Integer anioFallecimiento;
   // @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   @OneToMany(mappedBy = "autor", fetch = FetchType.EAGER)
   @JsonBackReference
    private List<Libro> libros = new ArrayList<>();

    //Constructor
    public Autor(){}

    //Constructor personalizado
    public Autor(DatosAutor datosAutor){
        //dejamos que la base de datos cree el id de autor
        //this.id = (long) datosAutor.id();
        this.nombreAutor = datosAutor.nombreAutor();
        this.anioNacimiento = datosAutor.anioNacimiento();
        this.anioFallecimiento = datosAutor.anioFallecimiento();
    }

    // Constructor personalizado con lista de libros
    public Autor(DatosAutor datosAutor, List<Libro> libros) {
        this(datosAutor); // Llama al otro constructor para inicializar los datos del autor
        this.libros = libros;
        for (Libro libro : libros) {
            libro.setAutor(this); // Establece este autor como el autor de cada libro
        }
    }

    //Getters
    public String getNombreAutor() {
        return nombreAutor;
    }

    public Integer getAnioNacimiento() {
        return anioNacimiento;
    }

    public Integer getAnioFallecimiento() {
        return anioFallecimiento;
    }
    public List<Libro> getLibros() {
        return libros;
    }

    //Setters
    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public void setAnioNacimiento(Integer anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public void setAnioFallecimiento(Integer anioFallecimiento) {
        this.anioFallecimiento = anioFallecimiento;
    }

    public void setLibros(List<Libro> libros) {
        libros.forEach(e -> e.setAutor(this));
        this.libros = libros;
    }

    @Override
    public String toString() {
        return
                " Nombre del autor='" + nombreAutor + '\'' +
                ", Fecha de nacimiento=" + anioNacimiento + '\'' +
                ", Fecha de fallecimiento=" + anioFallecimiento + '\''
              //  ", Libros=" + libros +
                ;
    }
}
