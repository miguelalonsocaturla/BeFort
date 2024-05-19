package com.example.befort;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Obtener referencias a los campos de entrada dentro de la vista inflada
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        // Inicializar la referencia a la base de datos de Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("usuarios");

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

                // Comprobar las credenciales del usuario en Firebase
                checkUserCredentials(email, password);
            }
        });

        // Devolver la vista inflada con las referencias configuradas
        return view;
    }

    private void checkUserCredentials(final String email, final String password) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userExists = false;
                String userType = null;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);
                    if (dbEmail != null && dbEmail.equals(email) && dbPassword != null && dbPassword.equals(password)) {
                        userExists = true;
                        userType = userSnapshot.child("tipo").getValue(String.class);
                        break;
                    }
                }

                if (userExists) {
                    // Inicio de sesión exitoso
                    Toast.makeText(getActivity(), "Inicio de sesión exitoso, Tipo: " + userType, Toast.LENGTH_SHORT).show();
                    // Aquí puedes manejar la variable userType como desees
                } else {
                    // Credenciales incorrectas, mostrar mensaje de error
                    Toast.makeText(getActivity(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la base de datos
                Toast.makeText(getActivity(), "Error al comprobar las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
