package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityEstadisticasBinding;

public class EstadisticasActivity extends AppCompatActivity {

    private ActivityEstadisticasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEstadisticasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recibimos los datos del Intent
        int correctas = getIntent().getIntExtra("correctas", 0);
        int total = getIntent().getIntExtra("total", 0);
        int sinResponder = getIntent().getIntExtra("sinResponder", 0);

        // Cálculo de respuestas incorrectas
        int incorrectas = total - correctas - sinResponder;

        binding.textViewCorrectas.setText(String.valueOf(correctas));
        binding.textViewIncorrectas.setText(String.valueOf(incorrectas));

        if (sinResponder > 0) {
            binding.layoutSinResponder.setVisibility(View.VISIBLE);
            binding.textViewSinResponderValor.setText(String.valueOf(sinResponder));
        } else {
            binding.layoutSinResponder.setVisibility(View.GONE);

        }

        //En caso se desee volver a jugar
        binding.btnVolverInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}