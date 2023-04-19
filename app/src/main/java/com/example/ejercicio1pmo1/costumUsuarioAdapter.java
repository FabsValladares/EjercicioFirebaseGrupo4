package com.example.ejercicio1pmo1;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ejercicio1pmo1.transacciones.usuarios;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class costumUsuarioAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<usuarios> Usuarios;

    public costumUsuarioAdapter(Context context, ArrayList<usuarios> usuario)
    {
        this.context = context;
        this.Usuarios = usuario;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return Usuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return Usuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.costum_usuario_listview, parent, false);
        }

        ImageView imagenImageView = convertView.findViewById(R.id.imageUsu);
        TextView nombreTextView = convertView.findViewById(R.id.txtnombre);
        usuarios usuario = Usuarios.get(position);

        Picasso.get().load(usuario.getUrlImagen()).into(imagenImageView);


        nombreTextView.setText(usuario.getNombre()+" "+usuario.getApellido()+"  "+usuario.getFechanac());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Pasar la información del producto seleccionado a la actividad de BiblioActivity
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("nombres",usuario.getNombre());
                intent.putExtra("apellidos",usuario.getApellido());
                intent.putExtra("fechanac",usuario.getFechanac());
                intent.putExtra("imagen", usuario.getUrlImagen());
                intent.putExtra("id", usuario.getId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}