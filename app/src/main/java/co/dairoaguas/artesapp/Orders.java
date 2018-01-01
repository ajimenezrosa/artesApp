package co.dairoaguas.artesapp;

public class Orders {
    private String id_producto;
    private String ivArticulo;
    private int precio;
    private String tvDescripcion;
    private int tvCantidad;
    private String tvEstado;
    private Boolean enable;

    public Orders(String id_producto, String ivArticulo, String tvDescripcion, int tvCantidad, int precio, String tvEstado, Boolean enable){
        this.id_producto = id_producto;
        this.ivArticulo = ivArticulo;
        this.tvDescripcion = tvDescripcion;
        this.precio = precio;
        this.tvCantidad = tvCantidad;
        this.tvEstado = tvEstado;
        this.enable = enable;
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

    public int getTvCantidad() {
        return tvCantidad;
    }

    public String getEstado() {
        return tvEstado;
    }

    public void setImagen(String ivArticulo){
        this.ivArticulo = ivArticulo;
    }

    public void setDescripcion(String tvDescripcion){
        this.tvDescripcion = tvDescripcion;
    }

    public void setTvCantidad(int tvCantidad) {
        this.tvCantidad = tvCantidad;
    }

    public void setEstado(String tvEstado){
        this.tvEstado= tvEstado;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }


}
