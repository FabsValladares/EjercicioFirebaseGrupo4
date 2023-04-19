package com.example.ejercicio1pmo1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    static final int Result_galeria = 101;
    Uri imageUri;
    EditText nombre, fecha, apellidos;
    String name, date, celular, apellido, id,image;
    Button Editar, TomarGaleria, eliminar;

    private FirebaseFirestore mifirestore;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private DatabaseReference myRef;

    ImageView imagen;
    ImageButton SelectedDate, volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        nombre = (EditText) findViewById(R.id.txtNombres);
        apellidos = (EditText) findViewById(R.id.txtApellidos);
        fecha = (EditText) findViewById(R.id.txtFecha);
        imagen = (ImageView) findViewById(R.id.imagen);
        eliminar = (Button) findViewById(R.id.eliminar);
        Editar = (Button) findViewById(R.id.Actualizar);
        TomarGaleria = (Button) findViewById(R.id.SeleccionarImage);
        volver = (ImageButton) findViewById(R.id.volver);
        SelectedDate = (ImageButton) findViewById(R.id.SeleccionarFecha);

        Intent intent = getIntent();
        String name = intent.getStringExtra("nombres");
        String lastname = intent.getStringExtra("apellidos");
        String date = intent.getStringExtra("fechanac");
        image = intent.getStringExtra("imagen");
        id = intent.getStringExtra("id");
        Picasso.get().load(image).into(imagen);

        mifirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        nombre.setText(name);
        apellidos.setText(lastname);
        fecha.setText(date);



        SelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = fecha.getText().toString();
                // Crear un objeto de calendario con la fecha actual
                Calendar calendar = Calendar.getInstance();

                // Crear un objeto DatePickerDialog y establecer la fecha actual como fecha predeterminada
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this,
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
        Editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nombre.getText().toString().trim();
                String apellido = apellidos.getText().toString().trim();
                String date = fecha.getText().toString().trim();

                if(name.isEmpty() && apellido.isEmpty() && date.isEmpty()){
                    Toast.makeText(EditActivity.this, "Lllene los campos solicitados", Toast.LENGTH_SHORT).show();
                }else{
                    UpdateUsuario(name,apellido,date);
                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
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

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacionEliminar();

            }
        });
    }
    private void mostrarDialogoConfirmacionEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar registro");
        builder.setMessage("¿Está seguro de que desea eliminar este registro?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EliminarUsuario();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No se hace nada, se cierra el diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void EliminarUsuario() {
      mifirestore.collection("usuario").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void unused) {
              Toast.makeText(EditActivity.this, "Usuario eliminado con Exito", Toast.LENGTH_SHORT).show();
              StorageReference imageRef = storage.getReferenceFromUrl(image);
              imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                      Toast.makeText(EditActivity.this, "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show();
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Toast.makeText(EditActivity.this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                  }
              });
             clear();
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Toast.makeText(EditActivity.this, "Error al eliminar Usuario", Toast.LENGTH_SHORT).show();
          }
      });
    }

    private void UpdateUsuario(String name, String apellido, String date) {
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

                            mifirestore.collection("usuario").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                Toast.makeText(EditActivity.this, "Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
                                    StorageReference imageRef = storage.getReferenceFromUrl(image);
                                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(EditActivity.this, "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditActivity.this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                clear();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditActivity.this, "Error al Actualizar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", name);
            map.put("apellido", apellido);
            map.put("fechanac", date);
            map.put("imagenUrl", image);

            mifirestore.collection("usuario").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditActivity.this, "Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
                    clear();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditActivity.this, "Error al Actualizar", Toast.LENGTH_SHORT).show();
                }
            });
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