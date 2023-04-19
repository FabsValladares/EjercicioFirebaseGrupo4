package com.example.ejercicio1pmo1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final int Result_galeria = 101;
    String POSTMethod, currentPath;

    Uri imageUri;
    EditText  nombre, fecha, apellidos;
    String name, date, celular, apellido;
    Button Guardar, TomarGaleria, verusuarios;

    private FirebaseFirestore  mifirestore;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private DatabaseReference myRef;

    ImageView imagen;
    ImageButton SelectedDate, volver;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mifirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        nombre = (EditText) findViewById(R.id.txtNombres);
        apellidos = (EditText) findViewById(R.id.txtApellidos);
        fecha = (EditText) findViewById(R.id.txtFecha);
        Guardar = (Button) findViewById(R.id.Actualizar);
        TomarGaleria = (Button) findViewById(R.id.SeleccionarImage);
        SelectedDate = (ImageButton) findViewById(R.id.SeleccionarFecha);
        imagen = (ImageView) findViewById(R.id.imagen);
        verusuarios = (Button) findViewById(R.id.eliminar);

        SelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = fecha.getText().toString();
                // Crear un objeto de calendario con la fecha actual
                Calendar calendar = Calendar.getInstance();

                // Crear un objeto DatePickerDialog y establecer la fecha actual como fecha predeterminada
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                // El usuario ha seleccionado una fecha
                                // Crear un objeto de calendario con la fecha seleccionada
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, dayOfMonth);

                                // Formatear la fecha como desees
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = dateFormat.format(selectedDate.getTime());

                                // Mostrar la fecha seleccionada en un TextView
                                fecha.setText(formattedDate);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                // Mostrar el diálogo de selección de fecha
                datePickerDialog.show();
            }
        });


        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nombre.getText().toString().trim();
                String apellido = apellidos.getText().toString().trim();
                String date = fecha.getText().toString().trim();

                if(name.isEmpty() && apellido.isEmpty() && date.isEmpty()){
                    Toast.makeText(MainActivity.this, "Lllene los campos solicitados", Toast.LENGTH_SHORT).show();
                }else{
                    postUsuario(name,apellido,date);
                }
            }
        });

        verusuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), usuariosListView.class);
                startActivity(intent);
            }
        });




        TomarGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GaleriaImagenes();
            }
        });
    }

    private void postUsuario(String name, String apellido, String date) {
        // Subir imagen a Firebase Storage
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("imagenes/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Obtener URL de descarga de la imagen
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // Agregar datos del usuario a Firestore
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombre", name);
                            map.put("apellido", apellido);
                            map.put("fechanac", date);
                            map.put("imagenUrl", imageUrl);

                            mifirestore.collection("usuario").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MainActivity.this, "Creado Exitosamente", Toast.LENGTH_SHORT).show();
                                    clear();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error al ingresar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "Seleccione una foto de la Galeria", Toast.LENGTH_SHORT).show();
        }
    }

    private void GaleriaImagenes()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Result_galeria);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode == RESULT_OK && requestCode == Result_galeria)
        {
            imageUri = data.getData();
            imagen.setImageURI(imageUri);
        }

    }

    private void clear(){
       nombre.setText("");
       apellidos.setText("");
       fecha.setText("");
       imagen.setImageResource(R.drawable.usuario);
    }

}