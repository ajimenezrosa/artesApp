package co.dairoaguas.artesapp;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.EventClick;


public class FragmentCategory extends Fragment implements View.OnClickListener {

    ImageView ivSombreros, ivBolsos, ivAccesorios;
    int CATEGORIA = 0;
    String id = "", email = "", user = "";
    EventClick eventoClick;

    public FragmentCategory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        getActivity().setTitle("Categorias");

        id = getArguments() != null ? getArguments().getString(Constants.ID) : "0";
        email = getArguments() != null ? getArguments().getString(Constants.EMAIL) : "0";
        user = getArguments() != null ? getArguments().getString(Constants.NAME) : "0";

        eventoClick = (EventClick) getContext();

        ivSombreros = view.findViewById(R.id.ivSombreros);
        ivBolsos = view.findViewById(R.id.ivBolsos);
        ivAccesorios = view.findViewById(R.id.ivAccesorios);

        ivSombreros.setImageBitmap(roundedCorner(((BitmapDrawable) getResources().getDrawable(R.drawable.sombrero)).getBitmap()));
        ivBolsos.setImageBitmap(roundedCorner(((BitmapDrawable) getResources().getDrawable(R.drawable.bolso)).getBitmap()));
        ivAccesorios.setImageBitmap(roundedCorner(((BitmapDrawable) getResources().getDrawable(R.drawable.accesorios)).getBitmap()));

        ivSombreros.setOnClickListener(this);
        ivBolsos.setOnClickListener(this);
        ivAccesorios.setOnClickListener(this);

        return view;
    }

    private Bitmap roundedCorner(Bitmap mbitmap) {
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 50, 50, mpaint);// Round Image Corner 100 100 100 100
        return imageRounded;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSombreros:
                CATEGORIA = 1;
                break;
            case R.id.ivBolsos:
                CATEGORIA = 2;
                break;
            case R.id.ivAccesorios:
                CATEGORIA = 3;
                break;
        }
        eventoClick.clickFragment(CATEGORIA, id, email, user);
    }
}
