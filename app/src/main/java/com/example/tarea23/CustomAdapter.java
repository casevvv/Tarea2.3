package com.example.tarea23;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea23.config.photograh;

import java.io.File;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private Cursor cursor; // Cursor para acceder a la base de datos
    private LayoutInflater mInflater;
    private Context mContext;

    public CustomAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_view_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        byte[] photo = cursor.getBlob(cursor.getColumnIndex(photograh.photo));
        String description = cursor.getString(cursor.getColumnIndex(photograh.description));

        if (photo != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.textView.setText(description);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.et_desc);
            imageView = itemView.findViewById(R.id.img);
        }
    }
}
