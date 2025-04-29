package com.example.lab3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean conexionExitosa = false;  // Variable de revisión de conexión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Spinners (dropdowns)
        String[] categorias = {"Cultura General", "Libros", "Películas", "Música", "Computación", "Matemática", "Deportes", "Historia"};
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(categoriaAdapter);

        String[] dificultades = {"fácil", "medio", "difícil"};
        ArrayAdapter<String> dificultadAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dificultades);
        dificultadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDificultad.setAdapter(dificultadAdapter);

        // Verificación de la conexión a interner :D
        binding.btnComprobarConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hayConexionInternet()) {
                    Toast.makeText(MainActivity.this, "¡Conexión exitosa!", Toast.LENGTH_SHORT).show();
                    conexionExitosa = true;
                    binding.btnComenzar.setEnabled(true); // Habilitado
                } else {
                    Toast.makeText(MainActivity.this, "¡Error de conexión!", Toast.LENGTH_SHORT).show();
                    conexionExitosa = false;
                    binding.btnComenzar.setEnabled(false); // Deshabilitado
                }
            }
        });
        binding.btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexionExitosa) {
                    if (validarFormulario()) {
                        irAJuego();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Primero comprueba tu conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Botón comenzar deshabilitado
        binding.btnComenzar.setEnabled(false);
    }

    // Método de botón para comprobar internet
    private boolean hayConexionInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnected());
        }
        return false;
    }

    private boolean validarFormulario() {
        // Validaciones
        //Validación de cantidad de texto
        String cantidadTexto = binding.editTextCantidad.getText().toString().trim();
        if (cantidadTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese la cantidad de preguntas", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Validación del número
        int cantidad = Integer.parseInt(cantidadTexto);
        if (cantidad <= 0) {
            Toast.makeText(this, "La cantidad debe ser un número positivo", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validación de categoría
        if (binding.spinnerCategoria.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validación de dificultad
        if (binding.spinnerDificultad.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione una dificultad", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void irAJuego() {
        // Obtenemos datos para empezar el juego
        String categoria = binding.spinnerCategoria.getSelectedItem().toString();
        String dificultad = binding.spinnerDificultad.getSelectedItem().toString();
        int cantidad = Integer.parseInt(binding.editTextCantidad.getText().toString().trim());

        // Enviamos esos datos al trivia activity
        Intent intent = new Intent(MainActivity.this, TriviaActivity.class);
        intent.putExtra("categoria", categoria);
        intent.putExtra("dificultad", dificultad);
        intent.putExtra("cantidad", cantidad);
        startActivity(intent);
    }
}