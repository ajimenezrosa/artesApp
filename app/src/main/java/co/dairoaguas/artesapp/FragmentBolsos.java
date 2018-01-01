package co.dairoaguas.artesapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.util.HashMap;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;


public class FragmentBolsos extends Fragment implements View.OnClickListener {

    EditText etTamaño, etColores, etTrenzado;
    Button btPedir;
    ImageView ivProducto;

    private static final String TAG = FragmentBolsos.class.getSimpleName();
    private Util util;

    String id_producto, id, email, imagen, descripcion;
    String CATEGORIA;
    int precio;

    public FragmentBolsos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bolsos, container, false);
        getActivity().setTitle("Bolsos");

        util = new Util(getContext());

        etTamaño = view.findViewById(R.id.etTamaño);
        etColores = view.findViewById(R.id.etColores);
        etTrenzado = view.findViewById(R.id.etTrenzado);
        btPedir = view.findViewById(R.id.btPedir);
        ivProducto = view.findViewById(R.id.ivProducto);

        id = getArguments() != null ? getArguments().getString(Constants.ID) : "0";
        email = getArguments() != null ? getArguments().getString(Constants.EMAIL) : "0";
        id_producto = getArguments() != null ? getArguments().getString(Constants.ID_ARTICLE) : "0";
        descripcion = getArguments() != null ? getArguments().getString(Constants.DESCRIPTION) : "0";
        imagen = getArguments() != null ? getArguments().getString(Constants.IMAGE) : "0";
        precio = getArguments() != null ? getArguments().getInt(Constants.PRICE) : 0;
        CATEGORIA = getArguments() != null ? getArguments().getString(Constants.CATEGORY) : "0";

        if (!imagen.isEmpty()) {
            Picasso.with(getContext()).invalidate(imagen);
            Picasso.with(getContext()).load(imagen).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivProducto);
        }

        etTamaño.setOnClickListener(this);
        etColores.setOnClickListener(this);
        etTrenzado.setOnClickListener(this);
        btPedir.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Bundle args = new Bundle();
        FragmentManager manager = getFragmentManager();
        DialogFragment dialogFragment = new FragmentListas();
        switch (view.getId()) {
            case R.id.etColores:
                args.putString("Selected", "1");
                dialogFragment.setArguments(args);
                dialogFragment.show(manager, "dialog");
                break;
            case R.id.etTamaño:
                args.putString("Selected", "2");
                dialogFragment.setArguments(args);
                dialogFragment.show(manager, "dialog");
                break;
            case R.id.etTrenzado:
                args.putString("Selected", "3");
                dialogFragment.setArguments(args);
                dialogFragment.show(manager, "dialog");
                break;
            case R.id.btPedir:
                setOrders();
                break;
        }
    }

    private void setOrders() {
        final String tamaño = etTamaño.getText().toString(),
                colores = etColores.getText().toString(), trenzado = etTrenzado.getText().toString();
        String url = Constants.SET_ORDERS;
        util.ShowLoading();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            util.showLogDebug(TAG + Constants.SET_ORDERS, "Message: " + response, "e");
                            JSONObject responseObject = new JSONObject(response);
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Snackbar.make(getView(), "Informacion: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    break;
                                case 1:
                                    Snackbar.make(getView(), descripcion + " se ha añadido al carrito de compras", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    break;
                                case 2:
                                    Snackbar.make(getView(), "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
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
                        util.showLogDebug(TAG, "Error Volley: " + error.getMessage(), "e");

                        Snackbar.make(getView(), "No se encuentra el servidor de destino: ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.CATEGORY, CATEGORIA);
                params.put(Constants.EMAIL, email);
                params.put(Constants.IMAGE, imagen);
                params.put(Constants.DESCRIPTION, descripcion);
                params.put(Constants.PRICE, String.valueOf(precio));
                params.put(Constants.CANTIDAD, "1");
                params.put(Constants.TURNS, "0");
                params.put(Constants.SIZE, tamaño);
                params.put(Constants.COLORS, colores);
                params.put(Constants.TRENZADO, trenzado);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
