package co.dairoaguas.artesapp;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class FragmentUpload extends Fragment implements View.OnClickListener {

    ViewFlipper viewFlipper;
    Bitmap bmp;
    ImageView iv1, iv2, iv3;
    Button btUpload;
    final static int cons = 0;
    int count = 0;

    boolean firstTime = true;


    public FragmentUpload() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        getActivity().setTitle("Subir mi diseño");

        iv1 = view.findViewById(R.id.iv1);
        iv2 = view.findViewById(R.id.iv2);
        iv3 = view.findViewById(R.id.iv3);

        btUpload = view.findViewById(R.id.btUpload);
        btUpload.setOnClickListener(this);

        btUpload.setEnabled(false);

        viewFlipper = view.findViewById(R.id.vfImages);
        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(2000);
        viewFlipper.removeView(iv2);
        viewFlipper.removeView(iv3);
        viewFlipper.setOnClickListener(this);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, cons);
                } else
                    Toast.makeText(getContext(), "Concede permisos de usar la camara para una mejor experiencia", Toast.LENGTH_SHORT).show();

            }
        });

        viewFlipper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                switch (viewFlipper.getCurrentView().getId()) {

                    case R.id.iv1:
                        Toast.makeText(getContext(), "La foto 1 ha sido eliminada " + count, Toast.LENGTH_SHORT).show();
                        iv1.setImageDrawable(getResources().getDrawable(R.drawable.icon_desing));
                        if (count != 0)
                            count--;
                        break;

                    case R.id.iv2:
                        Toast.makeText(getContext(), "La foto 2 ha sido eliminada ", Toast.LENGTH_SHORT).show();
                        viewFlipper.removeView(iv2);
                        if (count != 0)
                            count--;
                        break;

                    case R.id.iv3:
                        Toast.makeText(getContext(), "La foto 3 ha sido eliminada ", Toast.LENGTH_SHORT).show();
                        viewFlipper.removeView(iv3);
                        if (count != 0)
                            count--;
                        break;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btUpload:
                if (viewFlipper.getCurrentView() == null) {
                    Toast.makeText(getContext(), "No hay diseños cargados", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Se subio el diseño con exito", Toast.LENGTH_SHORT).show();
                break;

            case R.id.vfImages:
                if (firstTime) {
                    viewFlipper.stopFlipping();
                    firstTime = false;
                } else {
                    viewFlipper.showNext();
                    viewFlipper.startFlipping();
                    firstTime = true;
                }
                break;
        }
    }

    private boolean checkCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la camara!.");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 225);
            return false;
        } else {
            Log.i("Mensaje", "Tienes permiso para usar la camara.");
            return true;
        }
    }

    @Override
    public void onActivityResult(int requesCode, int resultCode, Intent data) {

        super.onActivityResult(requesCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle arg = data.getExtras();
            bmp = (Bitmap) arg.get("data");
            btUpload.setEnabled(true);
            if (bmp != null) {
                switch (count) {
                    case 0:
                        iv1.setImageBitmap(bmp);
                        break;
                    case 1:
                        iv2.setImageBitmap(bmp);
                        viewFlipper.addView(iv2);
                        break;
                    case 2:
                        iv3.setImageBitmap(bmp);
                        viewFlipper.addView(iv3);
                        break;
                    case 3:
                        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
                        fab.setEnabled(false);
                        Toast.makeText(getContext(), "No se aceptan mas fotos", Toast.LENGTH_SHORT).show();
                        break;
                }
                count++;
            } else {
                Toast.makeText(getContext(), "No se pudo cargar la imagen" + data, Toast.LENGTH_SHORT).show();
                Log.e("ERROR", "" + data);
            }
        }
    }
}
