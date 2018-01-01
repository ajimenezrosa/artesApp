package co.dairoaguas.artesapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Util util;

    EditText etName, etBirthDate, etEmail, etAddress, etPhone, etPass, etConfirmPass;
    Button btRegister;
    SharedPreferences preferences;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        util = new Util(this);
        preferences = getSharedPreferences("ArtesAppPreferences", this.MODE_PRIVATE);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        etName = findViewById(R.id.etName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etPass = findViewById(R.id.etPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btRegister = findViewById(R.id.btRegister);

        etBirthDate.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        setDateTimeField();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etBirthDate:
                datePickerDialog.show();
                break;
            case R.id.btRegister:
                if (validate())
                    setUser();
                break;
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, R.style.stylePrimary, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etBirthDate.setText(dateFormatter.format(newDate.getTime()));
                etEmail.setFocusable(true);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private boolean validate() {
        String name = etName.getText().toString(), email = etEmail.getText().toString(), birthday = etBirthDate.getText().toString(), address = etAddress.getText().toString(), phone = etPhone.getText().toString(), pass = etPass.getText().toString(), confirmpass = etConfirmPass.getText().toString();

        if (birthday.isEmpty()) {
            etBirthDate.setError("Introduzca una fecha de nacimiento válida");
            etBirthDate.requestFocus();
            return false;
        }

        if (name.isEmpty()) {
            etName.setError("Introduzca un nombre válido");
            etName.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            etAddress.setError("Introduzca una direccion válida");
            etAddress.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Introduzca una direccion válida");
            etPhone.requestFocus();
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

    private void setUser() {
        final String name = etName.getText().toString(), email = etEmail.getText().toString(), birthday = etBirthDate.getText().toString(), address = etAddress.getText().toString(), phone = etPhone.getText().toString(), pass = etPass.getText().toString();

        String url = Constants.REGISTER;
        util.ShowLoading();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            Util.showLogDebug(TAG + Constants.REGISTER, "Message: " + response, "d");
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Snackbar.make(etName, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    break;
                                case 1:
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("email", responseObject.getString(Constants.EMAIL));
                                    editor.putString("user", responseObject.getString(Constants.NAME));
                                    editor.putString("id", responseObject.getString(Constants.ID));
                                    editor.commit();

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra(Constants.NAME, responseObject.getString(Constants.NAME));
                                    i.putExtra(Constants.EMAIL, responseObject.getString(Constants.EMAIL));
                                    i.putExtra(Constants.ID, responseObject.getString(Constants.ID));
                                    startActivity(i);
                                    finishApp();
                                    break;
                                case 2:
                                    Snackbar.make(etName, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
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
                        Log.e("Error.Response", "" + error);
                        Snackbar.make(etName, "No se encuentra el servidor de destino: " + error, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.NAME, name);
                params.put(Constants.BIRTHDAY, birthday);
                params.put(Constants.EMAIL, email);
                params.put(Constants.ADDRESS, address);
                params.put(Constants.PHONE, phone);
                params.put(Constants.PASS, pass);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void finishApp() {
        this.finish();
    }
}
