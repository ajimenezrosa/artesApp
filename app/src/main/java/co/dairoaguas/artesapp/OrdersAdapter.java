package co.dairoaguas.artesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;


public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private List<Orders> orders;
    Context context;
    int precio;
    String id_producto, email, imagen, descripcion;
    private Util util;
    private static final String TAG = OrdersAdapter.class.getSimpleName();

    public class OrdersViewHolder extends RecyclerView.ViewHolder {


        public ImageView ivArticulo;
        public TextView tvPrecio;
        public TextView tvDescripcion;
        public TextView tvCantidad;
        public TextView tvEstado;
        public TextView tvDelete;
        public Button btMas, btMenos;

        public OrdersViewHolder(View view) {
            super(view);

            context = view.getContext();

            ivArticulo = view.findViewById(R.id.ivArticulo);
            tvPrecio = view.findViewById(R.id.tvPrecio);
            tvDescripcion = view.findViewById(R.id.tvDescripcion);
            tvCantidad = view.findViewById(R.id.tvCantidad);
            tvEstado = view.findViewById(R.id.tvEstado);
            tvDelete = view.findViewById(R.id.tvDelete);
            btMas = view.findViewById(R.id.btMas);
            btMenos = view.findViewById(R.id.btMenos);
        }
    }

    public OrdersAdapter(List<Orders> orders, int CATEGORIA, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_pedidos, parent, false);
        return new OrdersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OrdersViewHolder holder, final int position) {
        util = new Util(context);
        id_producto = orders.get(position).getId_producto();
        imagen = orders.get(position).getImagen();
        precio = orders.get(position).getPrecio();
        descripcion = orders.get(position).getDescripcion();

        if (orders.get(position).getImagen().isEmpty()) {
            holder.ivArticulo.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(context).invalidate(orders.get(position).getImagen());
            Picasso.with(context).load(orders.get(position).getImagen()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivArticulo);
        }

        if (orders.get(position).getEnable()) {
            holder.btMas.setVisibility(View.INVISIBLE);
            holder.btMenos.setVisibility(View.INVISIBLE);
        }

        holder.tvPrecio.setText(formatMoney(String.valueOf(orders.get(position).getPrecio())));
        holder.tvDescripcion.setText(orders.get(position).getDescripcion());
        holder.tvCantidad.setText(String.valueOf(orders.get(position).getTvCantidad()));
        holder.tvEstado.setText(orders.get(position).getEstado());

        int total = orders.get(position).getTvCantidad() * orders.get(position).getPrecio();
        holder.tvPrecio.setText(formatMoney(String.valueOf(total)));

        holder.btMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cantidad = Integer.valueOf(holder.tvCantidad.getText().toString());
                cantidad++;
                int total = cantidad * orders.get(position).getPrecio();
                holder.tvCantidad.setText(String.valueOf(cantidad));
                orders.get(position).setTvCantidad(cantidad);
                holder.tvPrecio.setText(formatMoney(String.valueOf(total)));
            }
        });

        holder.btMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cantidad = Integer.valueOf(holder.tvCantidad.getText().toString());

                if (cantidad == 1) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Confirmar");
                    dialog.setMessage("¿Desea eliminar articulo?");
                    dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            setOrderUpdate(id_producto);
                            orders.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, orders.size());
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.tvCantidad.setText(String.valueOf(1));
                                    orders.get(position).setTvCantidad(1);
                                }
                            });

                    final AlertDialog alert = dialog.create();
                    alert.show();

                } else {
                    cantidad--;
                    int total = cantidad * orders.get(position).getPrecio();
                    holder.tvCantidad.setText(String.valueOf(cantidad));
                    orders.get(position).setTvCantidad(cantidad);
                    holder.tvPrecio.setText(formatMoney(String.valueOf(total)));
                }
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Confirmar");
                dialog.setMessage("¿Desea eliminar articulo de su compra?");
                dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setOrderUpdate(id_producto);
                        orders.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, orders.size());
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });
    }

    public List<Orders> getOrders() {
        return this.orders;
    }

    public void addAll(List<Orders> orderList) {
        this.orders = orderList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setOrderUpdate(final String id) {
        String url = Constants.SET_ORDERS_UPDATE;
//        util.ShowLoading();
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            Util.showLogDebug(TAG + Constants.SET_ORDERS_UPDATE, "Message: " + response, "d");
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Toast.makeText(context, "Error: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(context, "Exito: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(context, "Error: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                            }
//                            util.HideLoading();

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            util.HideLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", "" + error);
                        Toast.makeText(context, "No se encuentra el servidor de destino: " + error, Toast.LENGTH_SHORT).show();
//                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.ID_ARTICLE, id);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public String formatMoney(String Valor) {
        double val = Double.parseDouble(Valor);
        DecimalFormat Num = new DecimalFormat("#,###");
        return "$" + Num.format(val);
    }
}
