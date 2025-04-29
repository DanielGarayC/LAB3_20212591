package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityTriviaBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriviaActivity extends AppCompatActivity {

    private ActivityTriviaBinding binding;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private int totalQuestions;
    private long totalTimeInMillis;
    private CountDownTimer countDownTimer;
    private String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener datos del intent
        String categoria = getIntent().getStringExtra("categoria");
        String dificultad = getIntent().getStringExtra("dificultad");
        int cantidad = getIntent().getIntExtra("cantidad", 5);

        binding.textViewCategoria.setText(categoria);

        // Tiempo total
        int segundosPorPregunta;
        switch (dificultad.toLowerCase()) {
            case "fácil":
                segundosPorPregunta = 5;
                break;
            case "medio":
                segundosPorPregunta = 7;
                break;
            case "difícil":
                segundosPorPregunta = 10;
                break;
            default:
                segundosPorPregunta = 5;
        }
        totalQuestions = cantidad;
        totalTimeInMillis = (long) cantidad * segundosPorPregunta * 1000;

        // Iniciar temporizador global
        startTimer();

        // Cargar preguntas
        fetchQuestions(cantidad, categoria, dificultad);

        binding.btnSiguiente.setOnClickListener(view -> {
            if (binding.radioGroupOpciones.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show();
            } else {
                verificarRespuesta();
                avanzarPregunta();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                int segundosRestantes = (int) (millisUntilFinished / 1000);
                binding.textViewTimer.setText(String.format(Locale.getDefault(), "00:%02d", segundosRestantes));
            }

            public void onFinish() {
                irAEstadisticas();
            }
        };
        countDownTimer.start();
    }

    private void fetchQuestions(int cantidad, String categoria, String dificultad) {
        int categoryId = mapearCategoria(categoria);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TriviaService apiService = retrofit.create(TriviaService.class);

        Call<TriviaRpta> call = apiService.getQuestions(
                cantidad,
                categoryId,
                dificultad.toLowerCase(),
                "boolean"
        );

        call.enqueue(new Callback<TriviaRpta>() {
            @Override
            public void onResponse(Call<TriviaRpta> call, Response<TriviaRpta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body().getResults();
                    mostrarPreguntaActual();
                } else {
                    Toast.makeText(TriviaActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TriviaRpta> call, Throwable t) {
                Toast.makeText(TriviaActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Mostrar la categoría según su número en la consulta en la API
    private int mapearCategoria(String categoria) {
        switch (categoria.toLowerCase()) {
            case "cultura general":
                return 9;
            case "libros":
                return 10;
            case "películas":
                return 11;
            case "música":
                return 12;
            case "computación":
                return 18;
            case "matemática":
                return 19;
            case "deportes":
                return 21;
            case "historia":
                return 23;
            default:
                return 9;
        }
    }
    //mostrarPreguntas
    private void mostrarPreguntaActual() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);
            binding.textViewPregunta.setText(currentQuestion.getQuestionText());
            binding.radioButtonOpcion1.setText("True");
            binding.radioButtonOpcion2.setText("False");
            binding.radioGroupOpciones.clearCheck();
            binding.textViewContadorPregunta.setText("Pregunta " + (currentQuestionIndex + 1) + "/" + totalQuestions);
        }
    }
    //Verificado de respuesta
    private void verificarRespuesta() {
        int selectedId = binding.radioGroupOpciones.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);
        selectedAnswer = selectedRadio.getText().toString();

        if (selectedAnswer.equalsIgnoreCase(questionList.get(currentQuestionIndex).getCorrectAnswer())) {
            correctAnswers++;
        }
    }
    //Siguiente pregunta
    private void avanzarPregunta() {
        currentQuestionIndex++;
        if (currentQuestionIndex >= totalQuestions) {
            irAEstadisticas();
        } else {
            mostrarPreguntaActual();
        }
    }

    //Envío a estadísticas
    private void irAEstadisticas() {
        countDownTimer.cancel();
        Intent intent = new Intent(this, EstadisticasActivity.class);
        intent.putExtra("correctas", correctAnswers);
        intent.putExtra("total", totalQuestions);
        startActivity(intent);
        finish();
    }
}