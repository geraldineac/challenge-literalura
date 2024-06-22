package com.aluracursos.literalura.principal;

import ch.qos.logback.core.encoder.JsonEscapeUtil;
import com.aluracursos.literalura.enums.IdiomasEnum;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.Datos;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.services.ConsumoAPI;
import com.aluracursos.literalura.services.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private List<Libro> libros;
    private List<Autor> autor;

    //Inyeccion de dependencias
    private LibroRepository repositorio;
    public Principal(LibroRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
//        var json = consumoAPI.obtenerDatos(URL_BASE);
//        System.out.println(json);
//        var datos = conversor.obtenerDatos(json, Datos.class);
//        System.out.println(datos);

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Búsqueda de libro por título 
                    2 - Lista de todos los libros
                    3 - Lista de autores
                    4 - Listar autores vivos en determinado año
                    5 - Exhibir libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    System.out.println(" Búsqueda de libro por título ");
                    buscarLibroWeb();
                    break;
                case 2:
                    System.out.println(" Lista de todos los libros ");
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    System.out.println(" Lista de todos los autores");
                    listaDeAutores();
                    break;
                case 4:
                    System.out.println(" Buscar autor vivos en determinado año");
                    autoresPorAnio();
                    break;
                case 5:
                    System.out.println(" Buscar libros por idioma ");
                    buscarLibroPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }


    private DatosLibro getDatosLibros() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
        System.out.println(json);

        Datos datos = conversor.obtenerDatos(json, Datos.class);
        List<DatosLibro> libros = datos.resultados();
//        datos.resultados().stream()
//                        .forEach(System.out::println);
//        System.out.println(datos);

        if (!libros.isEmpty()) {
            return libros.get(0);
        } else {
            return null; // Si no se encuentra ningún libro retorna null
        }
    }
    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibros();
        Libro libro = new Libro(datos);
        repositorio.save(libro);
        //datosLibros.add(datos);
        System.out.println("datos "+datos);

    }

    private void mostrarLibrosBuscados() {
        //datosSeries.forEach(System.out::println);
        libros = repositorio.findAll();
        //List<Serie> series = repositorio.findAll();
//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());

        libros.stream()
                //.sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarLibroPorIdioma(){

        System.out.println("Seleccione el idioma del libro que desea buscar:");
        System.out.println("1. Español");
        System.out.println("2. Inglés");
        System.out.println("3. Portugués");
        System.out.println("4. Francés");
        System.out.println("5. Italiano");
        System.out.print("Ingrese el número de la opción deseada: ");

        int opcion = Integer.parseInt(teclado.nextLine());
        IdiomasEnum idiomaSeleccionado;

        switch (opcion) {
            case 1:
                idiomaSeleccionado = IdiomasEnum.ES;
                break;
            case 2:
                idiomaSeleccionado = IdiomasEnum.EN;
                break;
            case 3:
                idiomaSeleccionado = IdiomasEnum.PT;
                break;
            case 4:
                idiomaSeleccionado = IdiomasEnum.FR;
                break;
            case 5:
                idiomaSeleccionado = IdiomasEnum.IT;
                break;
            default:
                System.out.println("Opción no válida. Se utilizará español por defecto.");
                idiomaSeleccionado = IdiomasEnum.ES;
        }

        List<Libro> librosPorIdioma = repositorio.findByIdiomas(idiomaSeleccionado);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma seleccionado: " + idiomaSeleccionado.name());
        } else {
            System.out.println("Libros encontrados en " + idiomaSeleccionado.name() + ":");
            librosPorIdioma.forEach(libro -> System.out.println("- " + libro.getTitulo()));
        }
    }

    private void listaDeAutores(){
        autor = repositorio.findAllUniqueAutores();

        if (autor.isEmpty()) {
            System.out.println("No se encontraron autores en la base de datos.");
        } else {
            System.out.println("Lista de autores:");
            autor.forEach(autor -> System.out.println("- " + autor.toString()));
        }
//        List<Libro> libros = repositorio.findAll();
//
//        // Extraer autores únicos de los libros
//        autor = libros.stream()
//                .map(Libro::getAutor)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (autor.isEmpty()) {
//            System.out.println("No se encontraron autores en la base de datos.");
//        } else {
//            System.out.println("Lista de autores:");
//            autor.forEach(autor -> System.out.println("- " + autor.getNombreAutor()));
//        }
    }
    private void autoresPorAnio(){
        System.out.print("Ingrese el año para buscar autores vivos: ");
        int anio = Integer.parseInt(teclado.nextLine());
        autor = repositorio.findByYearAutores(anio);

        if(autor.isEmpty()){
            System.out.println("No se encontraron autores vivos en el año " + anio);
        }else{
            System.out.println("Autores vivos en el año " + anio + ":");
            autor.forEach(autor -> {
                String estadoVital = autor.getAnioFallecimiento() == null ?
                        "Aún vivo" :
                        "Fallecido en " + autor.getAnioFallecimiento();
                System.out.println("- " + autor.getNombreAutor() +
                        " (Nacido en: " + autor.getAnioNacimiento() +
                        ", " + estadoVital + ")");
            });
        }
    }



}
