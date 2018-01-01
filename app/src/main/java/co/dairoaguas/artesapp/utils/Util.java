package co.dairoaguas.artesapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class Util {
    ProgressDialog pDialog;
    Context context;

    public Util(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Cargando...");
        pDialog.setTitle("ArtesApp");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        this.context = context;
    }

    public void ShowLoading() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void HideLoading() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public static void showLogDebug(String event, String message, String type) {
        switch (type) {
            case "d":
                Log.d(event, message);
                break;
            case "e":
                Log.e(event, message);
                break;
        }
    }

}
