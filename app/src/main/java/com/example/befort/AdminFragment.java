package com.example.befort;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AdminFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        Button cerrarSesionButton = view.findViewById(R.id.cerrar);
        Button newMaquinaButton = view.findViewById(R.id.newmaquina);
        Button newParqueButton = view.findViewById(R.id.newparque);

        cerrarSesionButton.setOnClickListener(v -> cerrarSesion());
        newMaquinaButton.setOnClickListener(v -> redirigirPestaña(R.id.action_adminFragment_to_newMaquinaFragment));
        newParqueButton.setOnClickListener(v -> redirigirPestaña(R.id.action_adminFragment_to_newParqueFragment));

        return view;
    }

    private void cerrarSesion() {
        // Limpia las SharedPreferences para cerrar sesión
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirige al usuario al LoginFragment
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.loginFragment);
    }

    private void redirigirPestaña (int r){
       NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
       navController.navigate(r);
    }

}
