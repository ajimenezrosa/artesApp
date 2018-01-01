package co.dairoaguas.artesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import co.dairoaguas.artesapp.utils.Constants;
import co.dairoaguas.artesapp.utils.EventClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EventClick {

    FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    boolean exit = true;
    SharedPreferences preferences;
    String id_user = "", email = "", name_user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("ArtesAppPreferences", this.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(Constants.ID);
        name_user = getIntent().getStringExtra(Constants.NAME);
        email = getIntent().getStringExtra(Constants.EMAIL);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView email_registred = header.findViewById(R.id.tvEmailRegistred);
        TextView user_registred = header.findViewById(R.id.tvUserRegistred);
        email_registred.setText(email);
        user_registred.setText(name_user);

        cargarFragmentCategory();
    }

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (exit)
                    exit_app();
                else {
                    cargarFragmentCategory();
                    navigationView.setCheckedItem(R.id.nav_choose);
                }
            }
        } else {
            fragmentManager.popBackStack();
        }
    }

    private void exit_app() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Estas seguro de salir?");
        dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }
        );
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exit) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("Confirmar");
            dialog.setMessage("¿Estas seguro de salir?");
            dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Delete".
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", "artesapp@gmail.com");
                    editor.putString("user", "ArtesApp");
                    editor.putString("id", "0");
                    editor.apply();
                    finish();
                }
            })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        exit = false;
        if (id == R.id.nav_choose) {
            cargarFragmentCategory();
            exit = true;
        } else if (id == R.id.nav_upload) {
            cargarFragmentUpload();
        } else if (id == R.id.nav_orders) {
            cargarFragmentOrders();
        } else if (id == R.id.nav_profile) {
            Profile();
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Conoce lo ultimo en artesanias, !" + getResources().getString(R.string.app_name) + "¡. Diseña tu producto y nosotros te lo realizamos en caña flecha.");
            startActivity(Intent.createChooser(intent, "Compartir " + getResources().getString(R.string.app_name)));
        } else if (id == R.id.nav_send) {
            android.app.FragmentManager manager = getFragmentManager();
            FragmentContact fragmentContact = new FragmentContact();
            fragmentContact.show(manager, "Dialog");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cargarFragmentCategory() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentCategory fragmentCategory = new FragmentCategory();
        args.putString(Constants.ID, this.id_user);
        args.putString(Constants.EMAIL, email);
        args.putString(Constants.NAME, name_user);
        fragmentCategory.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentCategory);
        fragmentManager.popBackStackImmediate();
        fragmentTransaction.commit();
        exit = true;
        navigationView.setCheckedItem(R.id.nav_choose);
    }

    private void cargarFragmentUpload() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentUpload fragmentupload = new FragmentUpload();
        fragmentupload.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentupload);
        fragmentTransaction.commit();
        navigationView.setCheckedItem(R.id.nav_upload);
    }

    private void cargarFragmentBolsos(String id_producto, String imagen, int precio, String descripcion, int CATEGORIA) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentBolsos fragmentBolsos = new FragmentBolsos();
        String categoria = String.valueOf(CATEGORIA);
        args.putString(Constants.CATEGORY, categoria);
        args.putString(Constants.ID, id_user);
        args.putString(Constants.GET_ARTICLES, id_producto);
        args.putString(Constants.EMAIL, email);
        args.putString(Constants.IMAGE, imagen);
        args.putString(Constants.DESCRIPTION, descripcion);
        args.putInt(Constants.PRICE, precio);
        fragmentBolsos.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentBolsos);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void cargarFragmentAccesorios(String id_producto, String imagen, int precio, String descripcion, int CATEGORIA) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentAccesorios fragmentAccesorios = new FragmentAccesorios();
        String categoria = String.valueOf(CATEGORIA);
        args.putString(Constants.CATEGORY, categoria);
        args.putString(Constants.ID, id_user);
        args.putString(Constants.GET_ARTICLES, id_producto);
        args.putString(Constants.EMAIL, email);
        args.putString(Constants.IMAGE, imagen);
        args.putString(Constants.DESCRIPTION, descripcion);
        args.putInt(Constants.PRICE, precio);
        fragmentAccesorios.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentAccesorios);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void cargarFragmentOrders() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentOrders fragmentOrders = new FragmentOrders();
        args.putString(Constants.ID, this.id_user);
        args.putString(Constants.EMAIL, email);
        fragmentOrders.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentOrders);
        fragmentTransaction.commit();
    }

    private void Profile() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        Profile profile = new Profile();
        args.putString(Constants.ID, this.id_user);
        args.putString(Constants.EMAIL, email);
        profile.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, profile);
        fragmentTransaction.commit();
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    private void cargarFragmentDetails(String id_producto, String imagen, int precio, String descripcion, int CATEGORIA) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        String categoria = String.valueOf(CATEGORIA);
        FragmentDetalleSombreros fragmentDetails = new FragmentDetalleSombreros();
        args.putString(Constants.CATEGORY, categoria);
        args.putString(Constants.ID, id_user);
        args.putString(Constants.GET_ARTICLES, id_producto);
        args.putString(Constants.EMAIL, email);
        args.putString(Constants.IMAGE, imagen);
        args.putString(Constants.DESCRIPTION, descripcion);
        args.putInt(Constants.PRICE, precio);
        fragmentDetails.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragmentDetails);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void cargarFragmentListas(int CATEGORIA, String id, String email, String user) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        FragmentList listFragment = new FragmentList();
        args.putInt(Constants.CATEGORY, CATEGORIA);
        args.putString(Constants.ID, id);
        args.putString(Constants.EMAIL, email);
        listFragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, listFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        navigationView.setCheckedItem(R.id.nav_choose);
    }

    @Override
    public void clickFragment(int Categoria, String id, String email, String user) {
        cargarFragmentListas(Categoria, id, email, user);
    }

    @Override
    public void callDetails(String id_producto, String imagen, int precio, String descripcion, int CATEGORIA) {
        switch (CATEGORIA) {
            case 1:
                cargarFragmentDetails(id_producto, imagen, precio, descripcion, CATEGORIA);
                break;
            case 2:
                cargarFragmentBolsos(id_producto, imagen, precio, descripcion, CATEGORIA);
                break;
            case 3:
                cargarFragmentAccesorios(id_producto, imagen, precio, descripcion, CATEGORIA);
                break;
        }
    }
}
