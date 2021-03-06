/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.mercado;

/**
 *
 * @author Candelaria
 */
public class TipoPuesto {
    
    private Integer id;
    private String nombre;
    private String descripcion;

    /**
    * Constructor por Defecto.
    */
    public TipoPuesto() {
    }

    /**
    * Constructor con parámetros.
     * @param nombre       
     * @param descripcion         
    */
    public TipoPuesto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
