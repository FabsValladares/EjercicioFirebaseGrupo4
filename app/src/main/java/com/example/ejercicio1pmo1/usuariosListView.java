package com.example.ejercicio1pmo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ejercicio1pmo1.R;
import com.example.ejercicio1pmo1.transacciones.usuarios;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class usuariosListView extends AppCompatActivity {
    private FirebaseFirestore mifirestore;
    private ListView UsuariosListView;
    FloatingActionButton agregar;
    costumUsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuarios_list_view);




        FirebaseApp.initializeApp(this);
        mifirestore = FirebaseFirestore.getInstance();
        UsuariosListView = findViewById(R.id.customListView);



        ArrayList<usuarios> usuario = obtenerusuariosFromFirebase();
        adapter = new  costumUsuarioAdapter(this, usuario);
        UsuariosListView.setAdapter(adapter);
        agregar = findViewById(R.id.registronuevo);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<usuarios> obtenerusuariosFromFirebase() {
        ArrayList<usuarios> listaUsuarios = new ArrayList<>();

        mifirestore.collection("usuario").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String nombre = documentSnapshot.getString("nombre");
                    String apellido = documentSnapshot.getString("apellido");
                    String fechaNacimiento = documentSnapshot.getString("fechanac");
                    String imageUrl = documentSnapshot.getString("imagenUrl");
                    String documentId = documentSnapshot.getId();
                    usuarios usuario = new usuarios(nombre, fechaNacimiento, apellido, imageUrl,documentId);
                    listaUsuarios.add(usuario);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(usuariosListView.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });

        return listaUsuarios;
    }
}