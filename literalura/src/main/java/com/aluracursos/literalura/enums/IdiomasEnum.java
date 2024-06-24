package com.aluracursos.literalura.enums;

public enum IdiomasEnum {
    ES("es", "es - español"),
    EN("en", "en - inglés"),
    PT("pt", "pt - portugués"),
    FR("fr", "fr - francés"),
    IT("it", "it - italiano");

    private String idiomas;
    private String expresionEnEspanol;

    IdiomasEnum(String idiomas, String expresionEnEspanol) {
        this.idiomas = idiomas;
        this.expresionEnEspanol = expresionEnEspanol;

    }

    IdiomasEnum(String idiomas) {
        this.idiomas = idiomas;
    }

    public String getExpresionEnEspanol() {
        return expresionEnEspanol;
    }

    public static IdiomasEnum fromString(String text) {
        for (IdiomasEnum idiomasEnum : IdiomasEnum.values()) {
            if (idiomasEnum.idiomas.equalsIgnoreCase(text)) {
                return idiomasEnum;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado: " + text);
    }
    public static IdiomasEnum fromEspanol(String text) {
        for (IdiomasEnum idiomasEnum : IdiomasEnum.values()) {
            if (idiomasEnum.expresionEnEspanol.equalsIgnoreCase(text)) {
                return idiomasEnum;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
}
