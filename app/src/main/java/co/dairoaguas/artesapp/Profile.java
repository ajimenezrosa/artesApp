package co.dairoaguas.artesapp;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;


public class Profile extends Fragment {

    private static final String TAG = Profile.class.getSimpleName();
    private Util util;

    EditText etName, etAddress, etPhone, etBirthDate, etEmail, etPass, etConfirmPass;
    Button btRegister;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    String id_user = "", email = "";

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Mi cuenta");

        util = new Util(getActivity());
        id_user = getArguments() != null ? getArguments().getString(Constants.ID) : "0";
        email = getArguments() != null ? getArguments().getString(Constants.EMAIL) : "0";
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        etBirthDate = view.findViewById(R.id.etBirthDate);
        etEmail = view.findViewById(R.id.etEmail);
        etPass = view.findViewById(R.id.etPass);
        etConfirmPass = view.findViewById(R.id.etConfirmPass);

        btRegister = view.findViewById(R.id.btRegister);

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate())
                    setUser();
            }
        });

        setDateTimeField();
        getUser();
        return view;
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getContext(), R.style.stylePrimary, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etBirthDate.setText(dateFormatter.format(newDate.getTime()));
                etEmail.setFocusable(true);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private boolean validate(){
        String name = etName.getText().toString(), email = etEmail.getText().toString(), birthday = etBirthDate.getText().toString(), pass = etPass.getText().toString(), confirmpass =etConfirmPass.getText().toString();

        if(name.isEmpty())
        {
            etName.setError("Introduzca un nombre válido");
            etName.requestFocus();
            return false;
        }

        if(birthday.isEmpty())
        {
            etBirthDate.setError("Introduzca una fecha de nacimiento válida");
            etBirthDate.requestFocus();
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduzca una dirección de correo electrónico válida");
            etEmail.requestFocus();
            return false;
        }

        if (pass.isEmpty() || pass.contains(" ") || pass.length() < 6 || pass.length() > 20) {
            etPass.setError("Introduzca entre 6 y 20 caracteres");
            etPass.requestFocus();
            return false;
        }

        if (confirmpass.isEmpty() || confirmpass.contains(" ") || confirmpass.length() < 6 || confirmpass.length() > 20) {
            etConfirmPass.setError("Introduzca entre 6 y 20 caracteres");
            etConfirmPass.requestFocus();
            return false;
        }

        if (!pass.equals(confirmpass)) {
            etConfirmPass.setError("Las contraseñas no coinciden");
            etConfirmPass.requestFocus();
            return false;
        }

        return true;
    }

    private void getUser() {

        String url = Constants.PROFILE;
        util.ShowLoading();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Util.showLogDebug(TAG + Constants.PROFILE, "Message: " + response, "e");
                            JSONObject responseObject = new JSONObject(response);
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Snackbar.make(btRegister, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    break;
                                case 1:
                                    etBirthDate.setText(responseObject.getString(Constants.BIRTHDAY));
                                    etName.setText(responseObject.getString(Constants.NAME));
                                    etAddress.setText(responseObject.getString(Constants.ADDRESS));
                                    etPhone.setText(responseObject.getString(Constants.PHONE));
                                    etEmail.setText(responseObject.getString(Constants.EMAIL));
                                    etPass.setText(responseObject.getString(Constants.PASS));
                                    etConfirmPass.setText(responseObject.getString(Constants.PASS));
                                    break;
                                case 2:
                                    Snackbar.make(btRegister, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
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
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", ""+error);
                        Snackbar.make(btRegister, "No se encuentra el servidor de destino: " + error, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(Constants.ID, id_user);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void setUser() {
        final String name = etName.getText().toString(), address = etAddress.getText().toString(), phone = etPhone.getText().toString(),
                email = etEmail.getText().toString(), birthday = etBirthDate.getText().toString(), pass = etPass.getText().toString();

        String url = Constants.PROFILE_UPDATE;
        util.ShowLoading();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            Util.showLogDebug(TAG + Constants.PROFILE_UPDATE, "Message: " + response, "d");
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Snackbar.make(btRegister, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    break;
                                case 1:
                                    Snackbar.make(btRegister, "Actualizacion exitosa", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    break;
                                case 2:
                                    Snackbar.make(btRegister, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
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
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", ""+error);
                        Snackbar.make(btRegister, "No se encuentra el servidor de destino: " + error, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(Constants.NAME, name);
                params.put(Constants.ADDRESS, address);
                params.put(Constants.PHONE, phone);
                params.put(Constants.BIRTHDAY, birthday);
                params.put(Constants.EMAIL, email);
                params.put(Constants.PASS, pass);
                return params;
            }
        };
        queue.add(postRequest);
    }

}
