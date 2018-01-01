package co.dairoaguas.artesapp;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


public class FragmentListas extends DialogFragment {

    String options;
    ListView lvOptions;

    ArrayList<String> alOptions = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listas, container, false);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        toolbar.setTitle("Elige una opcion");

        lvOptions = view.findViewById(R.id.lvOptions);

        options = getArguments().getString("Selected");


        switch (options)
        {
            case "1":
                toolbar.setTitle("Colores");
                String[] id_find = getResources().getStringArray(R.array.Colores);
                Collections.addAll(alOptions, id_find);
                break;

            case "2":
                toolbar.setTitle("Tamaño");
                String[] id_interest = getResources().getStringArray(R.array.Tamaño);
                Collections.addAll(alOptions, id_interest);
                break;

            case "3":
                toolbar.setTitle("Trenzado");
                String[] id_status = getResources().getStringArray(R.array.Trenzado);
                Collections.addAll(alOptions, id_status);
                break;

            case "4":
                toolbar.setTitle("Vueltas");
                String[] id_sons = getResources().getStringArray(R.array.Vueltas);
                Collections.addAll(alOptions, id_sons);
                break;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, alOptions );

        lvOptions.setAdapter(arrayAdapter);

        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (options)
                {
                    case "1":

                        EditText etColors = getActivity().findViewById(R.id.etColores);
                        etColors.setText(lvOptions.getItemAtPosition(position).toString());
                        break;

                    case "2":
                        EditText etTamaño = getActivity().findViewById(R.id.etTamaño);
                        etTamaño.setText(lvOptions.getItemAtPosition(position).toString());
                        break;

                    case "3":
                        EditText etTrenzado = getActivity().findViewById(R.id.etTrenzado);
                        etTrenzado.setText(lvOptions.getItemAtPosition(position).toString());
                        break;

                    case "4":
                        EditText etNumVueltas = getActivity().findViewById(R.id.etNumVueltas);
                        etNumVueltas.setText(lvOptions.getItemAtPosition(position).toString());
                        break;
                }

                dismiss();

            }
        });

        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }


}
