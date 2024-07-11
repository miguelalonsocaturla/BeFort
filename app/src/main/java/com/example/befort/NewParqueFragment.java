package com.example.befort;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.befort.model.Maquinas;
import com.example.befort.model.Parques;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewParqueFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText editTextNombreParque;
    private Spinner spinnerMaquinas;
    private Button btnAgregarMaquina;
    private TextView textViewMaquinasSeleccionadas;
    private List<Maquinas> maquinasDisponibles;
    private List<Maquinas> maquinasSeleccionadas;
    private double latitude,longitude;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_parque, container, false);
        // Inicializa
        editTextNombreParque = view.findViewById(R.id.editTextNombreParque);
        spinnerMaquinas = view.findViewById(R.id.spinnerMaquinas);
        btnAgregarMaquina = view.findViewById(R.id.btnAgregarMaquina);
        textViewMaquinasSeleccionadas = view.findViewById(R.id.textViewMaquinasSeleccionadas);
        maquinasDisponibles = new ArrayList<>();
        maquinasSeleccionadas = new ArrayList<>();
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // Método onMapReady será llamado cuando el mapa esté listo
        // Configurar Spinner con máquinas disponibles
        setupSpinner();
        // Configurar botón "Agregar Máquina"
        btnAgregarMaquina.setOnClickListener(v -> agregarMaquinaSeleccionada());
        // Configurar botón "Guardar Parque"
        Button btnGuardarParque = view.findViewById(R.id.btnGuardarParque);
        btnGuardarParque.setOnClickListener(v -> guardarParque());
        // Configurar botón "Guardar Parque"
        Button btnVolver = view.findViewById(R.id.volver);
        btnVolver.setOnClickListener(v -> volver());
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Configurar la ubicación predeterminada o mostrar la ubicación actual del usuario
        LatLng centroValencia = new LatLng(39.46975, -0.37739); // Coordenadas del centro de Valencia
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroValencia, 12f)); // Zoom nivel 12
        // Manejar clics en el mapa para obtener coordenadas
        googleMap.setOnMapClickListener(latLng -> {
            // Obtener latitud y longitud de la ubicación seleccionada
            googleMap.clear(); // Limpiar marcadores previos
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        });
    }
    private void setupSpinner() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference maquinasRef = db.collection("maquinas");

        maquinasRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            maquinasDisponibles.clear(); // Limpiar la lista antes de agregar nuevos datos
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Maquinas maquina = documentSnapshot.toObject(Maquinas.class);
                maquinasDisponibles.add(maquina);
            }
            // Actualizar el Spinner con las máquinas disponibles
            actualizarSpinner();
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Error al obtener documentos", e);
            Toast.makeText(requireContext(), "Error al cargar las máquinas disponibles", Toast.LENGTH_SHORT).show();
        });
    }
    private void actualizarSpinner() {
        List<String> nombresMaquinas = new ArrayList<>();
        for (Maquinas maquina : maquinasDisponibles) {
            nombresMaquinas.add(maquina.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresMaquinas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinas.setAdapter(adapter);
    }

    private void agregarMaquinaSeleccionada() {
        Maquinas maquinaSeleccionada = maquinasDisponibles.get(spinnerMaquinas.getSelectedItemPosition());
        if (maquinaSeleccionada != null) {
            maquinasSeleccionadas.add(maquinaSeleccionada);
            actualizarVistaMaquinasSeleccionadas();
            // Actualizar el Spinner después de agregar una máquina seleccionada
            maquinasDisponibles.remove(maquinaSeleccionada);
            actualizarSpinner();
        }
    }

    private void actualizarVistaMaquinasSeleccionadas() {
        StringBuilder sb = new StringBuilder();
        for (Maquinas maquina : maquinasSeleccionadas) {
            sb.append(maquina.getNombre()).append("\n");
        }
        textViewMaquinasSeleccionadas.setText(sb.toString());
    }

    private void guardarParque() {
        String nombreParque = editTextNombreParque.getText().toString().trim();
        if (nombreParque.isEmpty() || maquinasSeleccionadas.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Parques nuevoParque = new Parques();
        nuevoParque.setNombre(nombreParque);
        nuevoParque.setLatitud(latitude);
        nuevoParque.setLongitud(longitude);
        nuevoParque.setListaMaquinas(maquinasSeleccionadas);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("parques").add(nuevoParque)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Parque guardado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error al guardar parque", e);
                    Toast.makeText(requireContext(), "Error al guardar el parque", Toast.LENGTH_SHORT).show();
                });
    }

    private void volver(){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_newParqueFragment_to_adminFragment);
    }
}
