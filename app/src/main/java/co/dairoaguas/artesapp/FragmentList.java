package co.dairoaguas.artesapp;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;

public class FragmentList extends Fragment {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    List items;
    String id, email, user;
    int CATEGORIA;
    private Util util;
    private static final String TAG = FragmentList.class.getSimpleName();
    View vista;

    public FragmentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        getActivity().setTitle("DEPENDE");
        vista = view;

        util = new Util(getActivity());
        id = getArguments() != null ? getArguments().getString(Constants.ID) : "0";
        email = getArguments() != null ? getArguments().getString(Constants.EMAIL) : "0";
        user = getArguments() != null ? getArguments().getString(Constants.NAME) : "0";
        CATEGORIA = getArguments() != null ? getArguments().getInt(Constants.CATEGORY) : 0;

        switch (CATEGORIA) {
            case 1:
                getActivity().setTitle("Sombreros");
                break;

            case 2:
                getActivity().setTitle("Bolsos");
                break;

            case 3:
                getActivity().setTitle("Accesorios");
                break;
        }

        recycler = view.findViewById(R.id.rvListas);
        recycler.setHasFixedSize(true);

        lManager = new GridLayoutManager(view.getContext(), 2);
        recycler.setLayoutManager(lManager);

        getArticles(CATEGORIA);

        return view;
    }

    private void getArticles(int categoria) {

        final String type = String.valueOf(categoria).toString();
        util.ShowLoading();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, Constants.GET_ARTICLES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            util.showLogDebug(TAG + Constants.GET_ARTICLES, "Message: " + response, "e");
                            //markerList.clear();
                            JSONArray rest = new JSONArray(response);
                            items = new ArrayList();
                            items.clear();
                            Locale locale = new Locale("en", "US");
                            NumberFormat f = NumberFormat.getCurrencyInstance(locale);

                            for (int i = 0; i < rest.length(); i++) {
                                rest.getJSONObject(0).getString("success");
                                JSONObject jsonObject = (JSONObject) rest.get(i);
                                switch (jsonObject.getInt("success")) {
                                    case 0:
//                                        util.sneckbarError(fab, R.string.error);
                                        break;
                                    case 1:
                                        items.add(new Articulos(
                                                jsonObject.getString(Constants.ID_ARTICLE),
                                                jsonObject.getString(Constants.IMAGE),
                                                jsonObject.getString(Constants.DESCRIPTION),
                                                jsonObject.getInt(Constants.PRICE)));
                                        break;
                                    case 2:
                                        Snackbar.make(getView(), "Revisa tu conexion a internet, intenta nuevamente.", Snackbar.LENGTH_LONG)
                                                .setAction("Reintentar", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        getArticles(CATEGORIA);
                                                    }
                                                }).show();
                                        break;
                                }
                            }

                            if (items.size() != 0) {
                                adapter = new ArticulosAdapter(items, CATEGORIA, getContext());
                                recycler.setAdapter(adapter);
                            } else {
                                Snackbar.make(getView(), "No se encontraron articulos para mostrar.", Snackbar.LENGTH_LONG)
                                        .setAction("Reintentar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                getArticles(CATEGORIA);
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
                params.put(Constants.CATEGORY, type);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
