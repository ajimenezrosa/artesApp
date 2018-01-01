package co.dairoaguas.artesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import co.dairoaguas.artesapp.utils.EventClick;


public class ArticulosAdapter extends RecyclerView.Adapter<ArticulosAdapter.ArticulosViewHolder> {
    private List<Articulos> articulos;
    private int CATEGORIA;
    Context context;
    String id_producto, email, imagen, descripcion;
    int precio;

    public static class ArticulosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        public ImageView ivArticulo;
        public TextView tvPrecio;
        public TextView tvDescripcion;
        EventClick eventoClick;
        private int CATEGORIA, precio;
        String id_producto, imagen, descripcion;

        public ArticulosViewHolder(View view, int CATEGORIA) {
            super(view);

            context = view.getContext();
            this.CATEGORIA = CATEGORIA;

            eventoClick = (EventClick) context;

            ivArticulo = view.findViewById(R.id.ivArticulo);
            tvPrecio = view.findViewById(R.id.tvPrecio);
            tvDescripcion = view.findViewById(R.id.tvDescripcion);
        }

        void setOnClickListener(String id_producto, String imagen, int precio, String descripcion) {
            this.id_producto = id_producto;
            this.imagen = imagen;
            this.precio = precio;
            this.descripcion = descripcion;

            ivArticulo.setOnClickListener(this);
            tvPrecio.setOnClickListener(this);
            tvDescripcion.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivArticulo:
                    eventoClick.callDetails(id_producto, imagen, precio, descripcion, CATEGORIA);
                    break;
                case R.id.tvDescripcion:
                    eventoClick.callDetails(id_producto, imagen, precio, descripcion, CATEGORIA);
                    break;
            }
        }
    }

    public ArticulosAdapter(List<Articulos> articulos, int CATEGORIA, Context context) {
        this.articulos = articulos;
        this.CATEGORIA = CATEGORIA;
        this.context = context;
    }

    @Override
    public ArticulosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_articulos, parent, false);
        return new ArticulosViewHolder(v, CATEGORIA);
    }

    @Override
    public void onBindViewHolder(final ArticulosViewHolder holder, final int position) {
        id_producto = articulos.get(position).getId_producto();
        imagen = articulos.get(position).getImagen();
        precio = articulos.get(position).getPrecio();
        descripcion = articulos.get(position).getDescripcion();

        if (articulos.get(position).getImagen().isEmpty()) {
            holder.ivArticulo.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context).invalidate(articulos.get(position).getImagen());
            Picasso.with(context).load(articulos.get(position).getImagen()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivArticulo);
        }

        holder.tvPrecio.setText(formatMoney(String.valueOf(articulos.get(position).getPrecio())));
        holder.tvDescripcion.setText(articulos.get(position).getDescripcion());

        holder.setOnClickListener(id_producto, imagen, precio, descripcion);
    }


    @Override
    public int getItemCount() {
        return articulos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public String formatMoney(String Valor) {
        double val = Double.parseDouble(Valor);
        DecimalFormat Num = new DecimalFormat("#,###");
        return "$" + Num.format(val);
    }

}
