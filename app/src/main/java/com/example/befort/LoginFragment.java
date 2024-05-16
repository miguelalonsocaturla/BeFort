package com.example.befort;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Obtener referencias a los campos de entrada dentro de la vista inflada
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        // Obtener referencia al botón de inicio de sesión dentro de la vista inflada
        Button buttonLogin = view.findViewById(R.id.buttonLogin);

        // Configurar el evento de clic del botón de inicio de sesión
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener valores de los campos de entrada
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validar campos vacíos
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Por favor ingresa tu correo electrónico");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Por favor ingresa tu contraseña");
                    return;
                }

                // Aquí puedes agregar lógica adicional de autenticación
                // Por ejemplo, puedes realizar una solicitud de inicio de sesión a tu servidor

                // Simulando inicio de sesión exitoso para demostración
                if (email.equals("usuario@example.com") && password.equals("contraseña")) {
                    // Inicio de sesión exitoso, puedes mostrar un mensaje o realizar una acción
                    Toast.makeText(getActivity(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    // Ejemplo: getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                } else {
                    // Credenciales incorrectas, mostrar mensaje de error
                    Toast.makeText(getActivity(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Devolver la vista inflada con las referencias configuradas
        return view;
    }
}
