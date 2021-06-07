package com.example.photogallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class viewAdapter extends RecyclerView.Adapter<viewAdapter.ViewHolder>{
    final private List<Photo> localDataSet;

    public viewAdapter(List<Photo> localDataSet) {
        this.localDataSet = localDataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private String name;
        private final ImageView ivImage;
        private final TextView tvComentari;
        private final EditText etComentari;
        private final Button btnEditar;

        public ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivImage);
            tvComentari = view.findViewById(R.id.tvComentari);
            etComentari = view.findViewById(R.id.etComentari);
            btnEditar = view.findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(v -> {
                if (!etComentari.getText().toString().equals("")){
                    String comment = etComentari.getText().toString();
                    etComentari.setText("");
                    tvComentari.setText(comment);
                    PhotoComments.commentsToXML(v.getContext(), name, comment);
                } else {

                }
            });
        }

        public String getName() {
            return name;
        }
        public void setName (String name) {
            this.name = name;
        }
        public ImageView getivImage() {
            return ivImage;
        }
        public TextView gettvComentari() {
            return tvComentari;
        }
        public Button getBtnEditar() {
            return btnEditar;
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
        vHolder.setName(localDataSet.get(position).getNom());
        vHolder.getivImage().setImageBitmap(localDataSet.get(position).getBitmap());
        vHolder.gettvComentari().setText(localDataSet.get(position).getComentari());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
