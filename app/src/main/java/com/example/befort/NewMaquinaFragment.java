package com.example.befort;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.befort.model.Maquinas;
import com.google.firebase.firestore.FirebaseFirestore;


public class NewMaquinaFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_new_maquina, container, false);
        EditText editTextNombre = view.findViewById(R.id.editTextNombre);
        EditText editTextTipo = view.findViewById(R.id.editTextTipo);
        EditText editTextDescripcion = view.findViewById(R.id.editTextDescripcion);
        Button buttonAgregar = view.findViewById(R.id.buttonAgregar);

        buttonAgregar.setOnClickListener(view1 -> {
            String nombre = editTextNombre.getText().toString();
            String tipo = editTextTipo.getText().toString();
            String descripcion = editTextDescripcion.getText().toString();

            // Validar los campos (asegurarse de que no estén vacíos)
            if (nombre.isEmpty() || tipo.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this.getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear una nueva instancia de Maquinas con los datos ingresados
            Maquinas nuevaMaquina = new Maquinas();
            nuevaMaquina.setNombre(nombre);
            nuevaMaquina.setTipo(tipo);
            nuevaMaquina.setDescripcion(descripcion);


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("maquinas").add(nuevaMaquina);

            // Mostrar un mensaje
            Toast.makeText(this.getContext(), "Máquina añadida correctamente: " + nuevaMaquina.getNombre(), Toast.LENGTH_SHORT).show();


        });
        return view;
    }
}