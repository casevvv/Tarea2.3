package com.example.tarea23.config;

import com.example.tarea23.view_images;

public class photograh {
    //Nombre de la bd
    public static final String namedb = "Photograh";

    //Nombre de la tabla de la bd
    public static final String table = "fotografias";

    //Nombre de los campos de la bd
    public static final String id = "id";
    public static final String photo ="photo";
    public static final String description = "description";

    //Consultas BD
    public static final String CreateTablePhotograh = "CREATE TABLE fotografias"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, photo BLOB, description TEXT)";

    //DML
    public static final String DropTablePhotograh = "DROP TABLE IF EXISTS fotografias";

    //Seleccionar
    public static final String SelectTablePhotograh = "SELECT * FROM fotografias";

    public static final String InsertTablePhotograh =  "INSERT INTO "+table+"(photo,description) VALUES (?,?)";

}
