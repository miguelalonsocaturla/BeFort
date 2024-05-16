package com.example.befort;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.befort.model.Maquinas;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MaquinasFragment extends Fragment {

    private ListView lista;
    private EditText editTextBuscar;
    private Button botonBuscar;
    private List<Maquinas> maquinasList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinas, container, false);

        // Inicializar las vistas
        editTextBuscar = view.findViewById(R.id.editTextText2);
        botonBuscar = view.findViewById(R.id.button2);
        lista = view.findViewById(R.id.listaMaquinas);

        // Inicializar la lista de máquinas
        maquinasList = new ArrayList<>();

        // Obtener y mostrar la lista de máquinas
        obtenerListaMaquinas();

        // Configurar el OnClickListener para el botón de búsqueda
        botonBuscar.setOnClickListener(v -> {
            String textoBusqueda = editTextBuscar.getText().toString().trim();

            // Verificar la longitud del texto de búsqueda para realizar la búsqueda
            if (textoBusqueda.length() >= 3) {
                buscarMaquinaPorNombre(textoBusqueda);
            } else {
                // Si el texto de búsqueda es menor que 3 caracteres, mostrar la lista completa
                mostrarListaMaquinas();
            }
        });

        return view;
    }

    private void obtenerListaMaquinas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference maquinasRef = db.collection("maquinas");

        maquinasRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Maquinas maquina = documentSnapshot.toObject(Maquinas.class);
                maquinasList.add(maquina);
            }

            // Mostrar la lista de máquinas en el ListView
            mostrarListaMaquinas();

        }).addOnFailureListener(e -> {
            // Manejar errores al obtener datos de Firestore
            Log.e("FirebaseError", "Error al obtener documentos", e);
        });
    }

    private void mostrarListaMaquinas() {

        ArrayAdapter<Maquinas> adapter = configurarAdapter(maquinasList);
        lista.setAdapter(adapter);

        // Configurar el OnClickListener para mostrar detalles de una máquina al hacer clic en la lista
        lista.setOnItemClickListener((parent, view, position, id) -> mostrarDetallesMaquina(position));
    }


    private void mostrarDetallesMaquina(int position) {
        Maquinas maquina = maquinasList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(maquina.getNombre());
        builder.setMessage("Tipo: " + maquina.getTipo() + "\nDescripción: " + maquina.getDescripcion());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void buscarMaquinaPorNombre(String textoBusqueda) {
        List<Maquinas> resultadosBusqueda = new ArrayList<>();

        // Convertir el texto de búsqueda a minúsculas para una comparación insensible a mayúsculas/minúsculas
        String textoBusquedaLowerCase = textoBusqueda.toLowerCase();

        for (Maquinas maquina : maquinasList) {
            // Obtener el nombre de la máquina en minúsculas para la comparación
            String nombreMaquinaLowerCase = maquina.getNombre().toLowerCase();

            // Realizar la comparación para ver si el nombre de la máquina contiene el texto de búsqueda
            if (nombreMaquinaLowerCase.contains(textoBusquedaLowerCase)) {
                resultadosBusqueda.add(maquina);
            }
        }

        // Mostrar los resultados de la búsqueda en el ListView
        ArrayAdapter<Maquinas> adapter = configurarAdapter(resultadosBusqueda);
        lista.setAdapter(adapter);
    }

    private ArrayAdapter<Maquinas> configurarAdapter(List<Maquinas> listaMaquinas) {
        ArrayAdapter<Maquinas> adapter = new ArrayAdapter<Maquinas>(requireActivity(), android.R.layout.simple_list_item_1, listaMaquinas) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                // Obtener el objeto Maquinas en la posición actual desde la lista filtrada
                Maquinas maquina = listaMaquinas.get(position);

                // Configurar el texto del TextView con el nombre de la Maquina
                textView.setText(maquina.getNombre());

                return view;
            }
        };
        return adapter;
    }

}
