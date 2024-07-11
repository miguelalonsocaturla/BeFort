package com.example.befort;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.befort.model.Parques;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private GoogleMap googleMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestLocationPermission();
        setupMap();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    loadMarkers();
                    showUserLocation();
                }
            });
        }
    }

    private void loadMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parquesRef = db.collection("parques");

        parquesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Parques> parquesList = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                try {
                    Parques parques = documentSnapshot.toObject(Parques.class);
                    parquesList.add(parques);
                    addMarker(parques);
                } catch (RuntimeException e) {
                    Log.e("FirestoreError", "Error deserializing Parques", e);
                }
            }

            moveCameraToDefaultLocation();
        }).addOnFailureListener(e -> {
            Log.e("FirestoreError", "Error fetching Parques documents", e);
        });
    }

    private void addMarker(Parques parques) {
        LatLng latLng = new LatLng(parques.getLatitud(), parques.getLongitud());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(parques.getNombre()));
    }

    private void moveCameraToDefaultLocation() {
        LatLng valencia = new LatLng(39.46975, -0.37739);
        float zoomLevel = 12.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(valencia, zoomLevel));
    }


    private void showUserLocation() {
        if (googleMap != null) {
            // Habilitar la capa de ubicación del usuario en el mapa
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);

            // Obtener el proveedor de ubicación fusionada (FusedLocationProviderClient)
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            // Obtener la última ubicación conocida del dispositivo
            Task<Location> locationTask = fusedLocationClient.getLastLocation();

            // Manejar la respuesta exitosa y la ubicación obtenida
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    // Crear un objeto LatLng con la ubicación del usuario
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Animar la cámara para centrarse en la ubicación del usuario con un nivel de zoom de 15
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
                } else {
                    Log.e("LocationError", "No se encontró la ubicación del usuario");
                }
            }).addOnFailureListener(e -> {
                // Manejar cualquier error al obtener la ubicación
                Log.e("LocationError", "Error al obtener la ubicación del usuario", e);
            });
        }
    }


}
