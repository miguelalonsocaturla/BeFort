package com.example.befort;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        // Inicializar vistas y Firebase
        initViews(view);
        initFirebase();

        // Configurar el evento de clic del botón de registro
        configureRegisterButton(view);

        // Configurar el evento de clic del TextView para iniciar sesión
        configureLoginTextView(view);

        return view;
    }

    private void initViews(View view) {
        editTextEmail = view.findViewById(R.id.editTextEmail1);
        editTextPassword = view.findViewById(R.id.editTextPassword1);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void configureRegisterButton(View view) {
        Button buttonRegister = view.findViewById(R.id.buttonRegistro);
        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                registerUser(email, password);
            }
        });
    }

    private void configureLoginTextView(View view) {
        TextView textViewLogin = view.findViewById(R.id.textViewLogin);
        textViewLogin.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_registroFragment_to_loginFragment);
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Por favor ingresa tu correo electrónico");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Por favor ingresa tu contraseña");
            return false;
        }

        return true;
    }

    private void registerUser(String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("usuario", email);
        user.put("contraseña", password);
        user.put("tipo", false);

        db.collection("usuarios")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    saveUserType(false);
                    showToast("Registro exitoso");
                    NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.action_registroFragment_to_mapsFragment);
                })
                .addOnFailureListener(e -> showToast("Error al registrar el usuario"));
    }

    private void saveUserType(Boolean userType) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userType) {
            editor.putString("admin", "Y");
        } else {
            editor.putString("admin", "N");
        }
        editor.apply();
    }

    private void redirectToXScreen(View view) {

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
