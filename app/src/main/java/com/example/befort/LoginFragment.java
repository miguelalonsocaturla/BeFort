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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Comprueba si el usuario ya está logueado
        if (isUserLoggedIn()!=null) {
            // Redirige al usuario según el tipo de usuario almacenado
            redirectUser(view);
        }

        // Configurar el TextView para registrar una cuenta
        TextView textViewRegister = view.findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al fragmento de registro
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_loginFragment_to_registroFragment);
            }
        });

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
                checkUserCredentials(email, password, view);
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

    private void checkUserCredentials(final String email, final String password, View view) {
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

                            handleLoginSuccess(userType, view);
                        } else {
                            showToast("Credenciales incorrectas");
                        }
                    } else {
                        showToast("Error al comprobar las credenciales");
                    }
                });
    }

    private void handleLoginSuccess(Boolean userType, View view) {
        saveUserType(userType);
        showToast("Inicio de sesión exitoso");
        redirectUser(view);
    }

    private void saveUserType(Boolean userType) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convertir el valor booleano a cadena "Y" o "N"
        String userTypeString = userType ? "Y" : "N";

        editor.putString("admin", userTypeString); // Guardar el tipo de usuario como cadena
        editor.apply();
    }

    private String isUserLoggedIn() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("admin", null); // Obtener el tipo de usuario como cadena
    }

    private void redirectUser(View view) {
        String userType = isUserLoggedIn();


        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment_activity_main);
        if ("Y".equals(userType)) {
            navController.navigate(R.id.action_loginFragment_to_adminFragment);
        } else {
            navController.navigate(R.id.action_loginFragment_to_mapsFragment);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
