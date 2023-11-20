package com.example.tarea23;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.tarea23.config.photograh;
import com.example.tarea23.config.sqlConection;

public class view_images extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private SQLiteDatabase database;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar tu conexión a la base de datos
        sqlConection dbHelper = new sqlConection(this, "Photograh", null, 1);
        database = dbHelper.getWritableDatabase();

        // Obtener los datos de la tabla fotografias
        cursor = database.rawQuery(photograh.SelectTablePhotograh, null);

        adapter = new CustomAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el cursor y la base de datos cuando la actividad se destruye
        cursor.close();
        database.close();
    }

}