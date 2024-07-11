package com.example.befort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.befort.model.Maquinas;
import com.example.befort.model.Parques;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParquesFragment extends Fragment {

    private ListView lista;
    private EditText editTextBuscar;
    private Button botonBuscar;
    private List<Parques> parquesList;
    private SharedPreferences sharedPreferences;
    private boolean isAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parques, container, false);

        // Inicializar las vistas
        editTextBuscar = view.findViewById(R.id.editTextText2);
        botonBuscar = view.findViewById(R.id.button2);
        lista = view.findViewById(R.id.listaParques);

        // Inicializar SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isAdmin = "Y".equals(sharedPreferences.getString("admin", null));

        // Inicializar la lista de parques
        parquesList = new ArrayList<>();

        // Obtener y mostrar la lista de parques
        obtenerListaParques();

        // Configurar el OnClickListener para el botón de búsqueda
        botonBuscar.setOnClickListener(v -> {
            String textoBusqueda = editTextBuscar.getText().toString().trim();

            // Verificar la longitud del texto de búsqueda para realizar la búsqueda
            if (textoBusqueda.length() >= 3) {
                buscarParquePorNombre(textoBusqueda);
            } else {
                // Si el texto de búsqueda es menor que 3 caracteres, mostrar la lista completa
                mostrarListaParques();
            }
        });

        return view;
    }

    private void obtenerListaParques() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parquesRef = db.collection("parques");

        parquesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            parquesList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Parques parque = documentSnapshot.toObject(Parques.class);
                parque.setId(documentSnapshot.getId());  // Asignar el ID del documento
                parquesList.add(parque);
            }

            // Mostrar la lista de parques en el ListView
            mostrarListaParques();

        }).addOnFailureListener(e -> {
            // Manejar errores al obtener datos de Firestore
            Log.e("FirebaseError", "Error al obtener documentos", e);
        });
    }

    private void mostrarListaParques() {
        ArrayAdapter<Parques> adapter = configurarAdapter(parquesList);
        lista.setAdapter(adapter);

        // Configurar el OnClickListener para mostrar detalles de un parque al hacer clic en la lista
        lista.setOnItemClickListener((parent, view, position, id) -> mostrarDetallesParque(position));
    }

    private void mostrarDetallesParque(int position) {
        Parques parque = parquesList.get(position);

        List<Maquinas> maquinas = parque.getListaMaquinas();
        StringBuilder machinesText = new StringBuilder("Máquinas en este parque:\n-");
        for (Maquinas maquina : maquinas) {
            machinesText.append(maquina.getNombre()).append("\n-");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(parque.getNombre());
        builder.setMessage(machinesText.toString());
        builder.setPositiveButton("Ver en mapa", (dialog, which) -> {
            // Coordenadas y nombre del marcador
            double latitud = parque.getLatitud();
            double longitud = parque.getLongitud();
            String nombreMarcador = parque.getNombre();
            // Construir la URL para abrir Google Maps con el marcador en la ubicación específica
            String mapUrl = "geo:" + latitud + "," + longitud + "?q=" + latitud + "," + longitud + "(" + nombreMarcador + ")";
            // Crear un Intent para abrir un navegador con la ubicación y marcador
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            // Verificar si hay una actividad que pueda manejar la acción (abrir Google Maps)
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Manejar el caso en que no se pueda abrir Google Maps
                Toast.makeText(requireContext(), "No se puede abrir Google Maps", Toast.LENGTH_SHORT).show();
            }
        });

        // Si el usuario es administrador, mostrar el botón Eliminar
        if (isAdmin) {
            builder.setNegativeButton("Eliminar", (dialog, which) -> {
                eliminarParque(parque);
                dialog.dismiss();
            });
        }
        // Botón para cerrar el AlertDialog
        builder.setNeutralButton("Cerrar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void eliminarParque(Parques parque) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parquesRef = db.collection("parques");

        parquesRef.document(parque.getId()).delete().addOnSuccessListener(aVoid -> {
            Log.d("FirebaseSuccess", "Documento eliminado correctamente");
            parquesList.remove(parque);
            mostrarListaParques();
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Error al eliminar documento", e);
        });
    }

    private void buscarParquePorNombre(String textoBusqueda) {
        List<Parques> resultadosBusqueda = new ArrayList<>();

        // Convertir el texto de búsqueda a minúsculas para una comparación insensible a mayúsculas/minúsculas
        String textoBusquedaLowerCase = textoBusqueda.toLowerCase();

        for (Parques parque : parquesList) {
            // Obtener el nombre del parque en minúsculas para la comparación
            String nombreParqueLowerCase = parque.getNombre().toLowerCase();
            // Realizar la comparación para ver si el nombre del parque contiene el texto de búsqueda
            if (nombreParqueLowerCase.contains(textoBusquedaLowerCase)) {
                resultadosBusqueda.add(parque);
            }
        }
        // Mostrar los resultados de la búsqueda en el ListView
        ArrayAdapter<Parques> adapter = configurarAdapter(resultadosBusqueda);
        lista.setAdapter(adapter);
    }
    private ArrayAdapter<Parques> configurarAdapter(List<Parques> listaParques) {
        return new ArrayAdapter<Parques>(requireActivity(), android.R.layout.simple_list_item_1, listaParques) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                // Obtener el objeto Parques en la posición actual desde la lista filtrada
                Parques parque = listaParques.get(position);
                // Configurar el texto del TextView con el nombre del parque
                textView.setText(parque.getNombre());
                return view;
            }
        };
    }
}
