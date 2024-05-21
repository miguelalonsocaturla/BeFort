package com.example.befort;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar vistas y Firebase
        initViews(view);
        initFirebase();

        // Configurar el evento de clic del botón de inicio de sesión
        configureLoginButton(view);

        return view;
    }

    private void initViews(View view) {
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void configureLoginButton(View view) {
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                checkUserCredentials(email, password);
            }
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

    private void checkUserCredentials(final String email, final String password) {
        db.collection("usuarios")
                .whereEqualTo("usuario", email)
                .whereEqualTo("contraseña", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            Boolean userType = (Boolean) document.get("tipo");

                            handleLoginSuccess(userType);
                        } else {
                            showToast("Credenciales incorrectas");
                        }
                    } else {
                        showToast("Error al comprobar las credenciales");
                    }
                });
    }

    private void handleLoginSuccess(Boolean userType) {
        saveUserType(userType);
        showToast("Inicio de sesión exitoso");
        // Aquí puedes manejar la variable userType como desees

        // Si el tipo de usuario es true, inicia AdminActivity
        if (userType != null && userType) {
            Intent intent = new Intent(getActivity(), NewParqueFragment.class);
            startActivity(intent);
        } else {
            // Maneja la lógica para otros tipos de usuarios aquí
        }
    }

    private void saveUserType(Boolean userType) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("admin", userType);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
