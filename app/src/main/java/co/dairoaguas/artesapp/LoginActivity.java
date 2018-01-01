package co.dairoaguas.artesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.Util;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText etEmail, etPass;
    Button btLogin;
    String id = "", email = "";
    SharedPreferences preferences;

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        util = new Util(this);

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btLogin = findViewById(R.id.btLogin);

        btLogin.setOnClickListener(this);

        preferences = getSharedPreferences("ArtesAppPreferences", this.MODE_PRIVATE);

        email = preferences.getString("email", "artesapp@gmail.com");

        if (!email.equals("artesapp@gmail.com")) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Constants.NAME, preferences.getString("user", "ArtesApp"));
            i.putExtra(Constants.EMAIL, preferences.getString("email", "artesapp@gmail.com"));
            i.putExtra(Constants.ID, preferences.getString("id", "0"));
            startActivity(i);
            this.finish();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                if (validate())
                    getUser(etEmail.getText().toString(), etPass.getText().toString());
                break;
        }
    }

    private boolean validate() {
        String email = etEmail.getText().toString(), pass = etPass.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduzca una dirección de correo electrónico válida");
            etEmail.requestFocus();
            return false;
        }

        if (pass.isEmpty() || pass.contains(" ")) {
            etPass.setError("Introduzca una contraseña válida");
            etPass.requestFocus();
            return false;
        }
        return true;
    }

    private void clean() {
        etEmail.getText().clear();
        etPass.getText().clear();
    }

    private void getUser(final String email, final String pass) {
        String url = Constants.LOGIN;
        util.ShowLoading();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Util.showLogDebug(TAG + Constants.LOGIN, "Message: " + response, "e");
                            JSONObject responseObject = new JSONObject(response);
                            switch (responseObject.getInt(Constants.SUCCESS)) {
                                case 0:
                                    Snackbar.make(btLogin, responseObject.getString("message"), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    break;
                                case 1:
                                    id = responseObject.getString(Constants.ID);

                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("email", responseObject.getString(Constants.EMAIL));
                                    editor.putString("user", responseObject.getString(Constants.NAME));
                                    editor.putString("id", id);
                                    editor.commit();

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra(Constants.NAME, responseObject.getString(Constants.NAME));
                                    i.putExtra(Constants.EMAIL, responseObject.getString(Constants.EMAIL));
                                    i.putExtra(Constants.ID, id);
                                    startActivity(i);
                                    clean();
                                    finishApp();
                                    break;
                                case 2:
                                    Snackbar.make(btLogin, "Error: " + responseObject.get("message"), Snackbar.LENGTH_LONG)
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
                        Util.showLogDebug(TAG, "Error Volley: " + error.getMessage(), "e");

                        Snackbar.make(btLogin, "No se encuentra el servidor de destino: ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        util.HideLoading();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.EMAIL, email);
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
