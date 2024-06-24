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
                    \n *** SELECCIONE UNA OPCIÓN DEL MENU *** \n
                    1 - Búsqueda de libro por título 
                    2 - Lista de todos los libros
                    3 - Lista de autores
                    4 - Listar autores vivos en determinado año
                    5 - Exhibir libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.println("\uD83D\uDCD6 Búsqueda de libro por título ");
                        buscarLibroWeb();
                        break;
                    case 2:
                        System.out.println("\uD83D\uDCD6 Lista de todos los libros ");
                        mostrarLibrosBuscados();
                        break;
                    case 3:
                        System.out.println("\uD83D\uDCD6 Lista de todos los autores \n");
                        listaDeAutores();
                        break;
                    case 4:
                        System.out.println("\uD83D\uDCD6 Buscar autor vivos en determinado año");
                        autoresPorAnio();
                        break;
                    case 5:
                        System.out.println("\uD83D\uDCD6 Buscar libros por idioma ");
                        buscarLibroPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida, elija otra opción");
                }
            } catch(InputMismatchException e){
                System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 5.");
                teclado.nextLine();
            }
        }


    }


    private DatosLibro getDatosLibros() {
        System.out.println("\n *** Escribe el nombre del libro que deseas buscar *** \n");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "%20"));
        //System.out.println(json);

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
        if(datos != null) {
            Optional<Libro> tituloYaExiste = repositorio.findByTituloContainsIgnoreCase(datos.titulo());

            if(tituloYaExiste.isPresent()){
                System.out.println("\n *** El libro ya existe en la base de datos ***\n");
            } else{
                Libro libro = new Libro(datos);
                repositorio.save(libro);
                System.out.println("\n *** Libro agregado a base de datos ***\n");
                //datosLibros.add(datos);
                System.out.println(" *** *** *** *** \n");
                System.out.printf("""
                    Titulo: %s
                    Idioma: %s
                    Numero de descargas: %s
                    Autor: %s ( %s - %s ) \n
                    """, datos.titulo(), IdiomasEnum.fromString(datos.idiomas().get(0)).getExpresionEnEspanol(), datos.numeroDeDescargas(),
                        datos.autor().get(0).nombreAutor(),
                        (datos.autor().get(0).anioNacimiento() != null ) ? datos.autor().get(0).anioNacimiento(): "fecha de nacimiento desconocida",
                        (datos.autor().get(0).anioFallecimiento() != null ) ? datos.autor().get(0).anioFallecimiento() : " " );
                System.out.println(" *** *** *** *** \n");

            }

        } else {
            System.out.println("\n *** No se encontró el libro o el autor que intentas buscar *** \n");
        }

       // System.out.println("datos "+datos);

    }

    private void mostrarLibrosBuscados() {
        //datosSeries.forEach(System.out::println);
        libros = repositorio.findAll();
        //List<Serie> series = repositorio.findAll();
//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());
            if (libros.isEmpty()) {
                System.out.println("\n *** No hay libros en la base de datos *** \n");
            } else {
                System.out.println("\n *** *** *** *** ");
                libros.stream()
                        .sorted(Comparator.comparing(Libro::getTitulo))
                        .forEach(l -> System.out.printf("""
                                Libro: %s
                                Idioma: %s
                                Numero de descargas: %s
                                Autor: %s \n
                                """, l.getTitulo(), l.getIdiomas().getExpresionEnEspanol(), l.getNumeroDeDescargas(), l.getAutor().getNombreAutor()));
                System.out.println(" *** *** *** *** \n");
            }

    }

    private void buscarLibroPorIdioma(){

        System.out.println("\n *** Seleccione el idioma del libro que desea buscar *** \n");
        System.out.println("1. Español");
        System.out.println("2. Inglés");
        System.out.println("3. Portugués");
        System.out.println("4. Francés");
        System.out.println("5. Italiano");
        System.out.print("\n  Ingrese el número de la opción deseada:  ");
        try {
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
                System.out.println("\n *** No se encontraron libros en el idioma seleccionado: " + idiomaSeleccionado.name() + " *** \n");
            } else {
                //idiomaSeleccionado.name()
                System.out.println("\n *** Libros encontrados en (" + idiomaSeleccionado.getExpresionEnEspanol() + ") *** \n");
                librosPorIdioma.forEach(libro -> System.out.printf("""
                                Titulo: %s
                                Autor: %s \n
                                """,
                        libro.getTitulo(), libro.getAutor().getNombreAutor()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 5.");
        }
    }

    private void listaDeAutores(){
        autor = repositorio.findAllUniqueAutores();

        if (autor.isEmpty()) {
            System.out.println("\n *** No se encontraron autores en la base de datos *** \n");
        } else {
            autor.forEach(autor -> {
                String titulos = autor.getLibros().stream()
                            .map(Libro::getTitulo)
                            .collect(Collectors.joining(", "));
                        System.out.printf(
                                """                                      
                                Autor: %s ( %s - %s )
                                Titulos: %s \n
                                """, autor.getNombreAutor(),
                                autor.getAnioNacimiento(), autor.getAnioFallecimiento(), titulos);
            });
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
        System.out.print("\n *** Ingrese el año para buscar autores vivos:  *** \n");
        int anio = Integer.parseInt(teclado.nextLine());
        autor = repositorio.findByYearAutores(anio);

        if(autor.isEmpty()){
            System.out.println("\n *** No se encontraron autores vivos en el año " + anio + " *** \n");
        }else{
            System.out.println("\n *** Autores vivos en el año " + anio + " *** \n");
            autor.forEach(autor -> {
                String estadoVital = autor.getAnioFallecimiento() == null ?
                        "Aún vivo" :
                        "Fallecido en " + autor.getAnioFallecimiento();
                System.out.println("- " + autor.getNombreAutor() +
                        " (Nacido en: " + autor.getAnioNacimiento() +
                        ", " + estadoVital + ") \n");
            });
        }
    }



}
