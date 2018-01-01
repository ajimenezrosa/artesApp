package co.dairoaguas.artesapp;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;

public class FragmentOrders extends Fragment {

    private RecyclerView recycler;
    private OrdersAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private Button btComprar;

    List items;
    String id, email;
    int CATEGORIA;
    private Util util;
    private static final String TAG = FragmentOrders.class.getSimpleName();

    public FragmentOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        getActivity().setTitle("Mis pedidos");

        util = new Util(getActivity());

        id = getArguments() != null ? getArguments().getString(Constants.ID) : "0";
        email = getArguments() != null ? getArguments().getString(Constants.EMAIL) : "0";
        CATEGORIA = getArguments() != null ? getArguments().getInt(Constants.CATEGORY) : 0;

        recycler = view.findViewById(R.id.rvOrders);
        recycler.setHasFixedSize(true);
        btComprar = view.findViewById(R.id.btComprar);

        lManager = new LinearLayoutManager(view.getContext());
        recycler.setLayoutManager(lManager);

        getOrders();


        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Orders> newOrderList = new ArrayList<>();
                List<Orders> ordersList = adapter.getOrders();

                if (ordersList.size() != 0) {

                    for (Orders order : ordersList) {

                        if (order.getEstado().equals("ESTADO: DISPONIBLE PARA COMPRA")) {
                            setBuy(order.getId_producto(), order.getTvCantidad());
                            order.setEstado("ESTADO: COMPRADO Y LISTO PARA ENVIAR");
                            order.setEnable(true);
                        }

                        newOrderList.add(order);
                    }
                    adapter.addAll(newOrderList);
                } else
                    Toast.makeText(getContext(), "No hay articulos en el carrito de compras", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getOrders() {

        util.ShowLoading();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.GET_ORDERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            util.showLogDebug(TAG + Constants.GET_ORDERS, "Message: " + response, "e");
                            JSONArray rest = new JSONArray(response);
                            items = new ArrayList();
                            items.clear();

                            for (int i = 0; i < rest.length(); i++) {
                                rest.getJSONObject(i).getString("success");
                                JSONObject jsonObject = (JSONObject) rest.get(i);
                                switch (jsonObject.getInt("success")) {
                                    case 0:
//                                        util.sneckbarError(fab, R.string.error);
                                        break;
                                    case 1:

                                        String estado;
                                        Boolean enable = true;
                                        if (jsonObject.getString(Constants.READY).equals("1")) {
                                            estado = "ESTADO: RECIBIDO";
                                        } else if (jsonObject.getString(Constants.SEND).equals("1")) {
                                            estado = "ESTADO: ENVIADO";
                                        } else if (jsonObject.getString(Constants.BUY).equals("1")) {
                                            estado = "ESTADO: COMPRADO Y LISTO PARA ENVIAR";
                                        } else {
                                            estado = "ESTADO: DISPONIBLE PARA COMPRA";
                                            enable = false;
                                        }

                                        items.add(new Orders(
                                                jsonObject.getString(Constants.ID_ARTICLE),
                                                jsonObject.getString(Constants.IMAGE),
                                                jsonObject.getString(Constants.DESCRIPTION),
                                                jsonObject.getInt(Constants.CANTIDAD),
                                                jsonObject.getInt(Constants.PRICE),
                                                estado,
                                                enable));
                                        break;
                                    case 2:
                                        Snackbar.make(getView(), "Revisa tu conexion a internet, intenta nuevamente.", Snackbar.LENGTH_LONG)
                                                .setAction("Reintentar", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        getOrders();
                                                    }
                                                }).show();
                                        break;
                                }
                            }
                            adapter = new OrdersAdapter(items, CATEGORIA, getContext());
                            if (items.size() != 0) {
                                recycler.setAdapter(adapter);
                            } else {
                                Snackbar.make(getView(), "No se encontraron articulos para mostrar.", Snackbar.LENGTH_LONG)
                                        .setAction("Reintentar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                getOrders();
                                            }
                                        }).show();
                            }

                            util.HideLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.HideLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        util.showLogDebug(TAG, "Error Volley: " + error.getMessage(), "e");
                        util.HideLoading();

                        Toast.makeText(getContext(), "Revisa tu conexion a internet, intenta nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.EMAIL, email);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void setBuy(final String id, final int cantidad) {

        String url = Constants.SET_BUY;
        util.ShowLoading();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            Util.showLogDebug(TAG + Constants.SET_BUY, "Message: " + response, "d");
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Toast.makeText(getContext(), "Error: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(getContext(), "Exito: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(getContext(), "Error: " + responseObject.get("message"), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            util.HideLoading();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.HideLoading();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", "" + error);
                        Toast.makeText(getContext(), "No se encuentra el servidor de destino: " + error, Toast.LENGTH_SHORT).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.ID_ARTICLE, id);
                params.put(Constants.CANTIDAD, String.valueOf(cantidad));
                return params;
            }
        };
        queue.add(postRequest);
    }
}
