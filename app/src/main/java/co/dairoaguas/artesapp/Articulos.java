package co.dairoaguas.artesapp;

public class Articulos {
    private String id_producto;
    private String ivArticulo;
    private int precio;
    private String tvDescripcion;

    public Articulos(String id_producto, String ivArticulo, String tvDescripcion, int precio) {
        this.id_producto = id_producto;
        this.ivArticulo = ivArticulo;
        this.tvDescripcion = tvDescripcion;
        this.precio = precio;
    }

    public String getImagen() {
        return ivArticulo;
    }

    public String getDescripcion() {
        return tvDescripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setArticulo(String tvDescripcion) {
        this.tvDescripcion = tvDescripcion;
    }
}
