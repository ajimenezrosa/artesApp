package co.dairoaguas.artesapp;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.Toast;

public class FragmentContact extends DialogFragment {

    private RadioButton rbTwitter, rbFacebook, rbEmail, rbWeb;
    FloatingActionButton fabSendContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        @SuppressLint("RestrictedApi") final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.stylePrimary);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.fragment_contact, container, false);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar_contact);
        toolbar.setTitle("Contactenos");

        rbTwitter = view.findViewById(R.id.rbTwitter);
        rbFacebook = view.findViewById(R.id.rbFacebook);
        rbEmail = view.findViewById(R.id.rbEmail);
        rbWeb = view.findViewById(R.id.rbWeb);
        fabSendContact = view.findViewById(R.id.fbSendContact);

        rbWeb.setChecked(true);

        fabSendContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbTwitter.isChecked()) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=Dairo_Aguas")));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Dairo_Aguas")));
                    }
                } else if (rbFacebook.isChecked()) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Dairo.Aguas")));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Dairo.Aguas")));
                    }
                } else if (rbEmail.isChecked()) {
                    sendEmail();
                } else if (rbWeb.isChecked()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ondasinnovacion.com/")));
                }
            }
        });
        return view;
    }

    protected void sendEmail() {
        String[] TO = {"info@ondasinnovacion.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contactando con ArtesApp");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe lo que quieras comunicarnos.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar..."));
            dismiss();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}