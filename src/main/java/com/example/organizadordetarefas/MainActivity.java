package com.example.organizadordetarefas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText editTarefa;
    Button botaoAdicionar, botaoHora, botaoData;
    TextView textHora, textData;
    ListView listaTarefas;

    ArrayList<String> tarefas;
    ArrayAdapter<String> adapter;

    String horaSelecionada = "";
    String dataSelecionada = "";

    Calendar calendarSelecionado;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTarefa = findViewById(R.id.editTarefa);
        botaoAdicionar = findViewById(R.id.botaoAdicionar);
        botaoHora = findViewById(R.id.botaoHora);
        botaoData = findViewById(R.id.botaoData);
        textHora = findViewById(R.id.textHora);
        textData = findViewById(R.id.textData);
        listaTarefas = findViewById(R.id.listaTarefas);

        calendarSelecionado = Calendar.getInstance();

        preferences = getSharedPreferences("tarefas", MODE_PRIVATE);

        tarefas = new ArrayList<>();

        carregarTarefas();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tarefas
        );

        listaTarefas.setAdapter(adapter);

        botaoData.setOnClickListener(v -> selecionarData());

        botaoHora.setOnClickListener(v -> selecionarHora());

        botaoAdicionar.setOnClickListener(v -> adicionarTarefa());

        listaTarefas.setOnItemClickListener((parent, view, position, id) -> {

            tarefas.remove(position);
            salvarTarefas();
            adapter.notifyDataSetChanged();

        });
    }

    private void selecionarData() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    calendarSelecionado.set(Calendar.YEAR, year);
                    calendarSelecionado.set(Calendar.MONTH, month);
                    calendarSelecionado.set(Calendar.DAY_OF_MONTH, day);

                    dataSelecionada = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d",
                            day,
                            month + 1,
                            year);

                    textData.setText("Data: " + dataSelecionada);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void selecionarHora() {

        Calendar calendar = Calendar.getInstance();

        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {

                    calendarSelecionado.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendarSelecionado.set(Calendar.MINUTE, minute);

                    horaSelecionada = String.format(Locale.getDefault(),
                            "%02d:%02d",
                            hourOfDay,
                            minute);

                    textHora.setText("Hora: " + horaSelecionada);

                },
                hora,
                minuto,
                true
        );

        dialog.show();
    }

    private void adicionarTarefa() {

        String tarefa = editTarefa.getText().toString();

        if (tarefa.isEmpty()) {
            Toast.makeText(this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            return;
        }

        String tarefaCompleta =
                dataSelecionada + " " + horaSelecionada + " - " + tarefa;

        tarefas.add(tarefaCompleta);

        Collections.sort(tarefas);

        salvarTarefas();

        adapter.notifyDataSetChanged();

        editTarefa.setText("");
    }

    private void salvarTarefas() {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putStringSet("lista", new HashSet<>(tarefas));

        editor.apply();
    }

    private void carregarTarefas() {

        Set<String> set = preferences.getStringSet("lista", new HashSet<>());

        tarefas.addAll(set);
    }

}