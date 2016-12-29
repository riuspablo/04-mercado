/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.mercado.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import poo.mercado.Cliente;
import poo.mercado.Contrato;
import poo.mercado.Dimension;
import poo.mercado.Empleado;
import poo.mercado.Estado;
import poo.mercado.Puesto;
import poo.mercado.Sesion;
import poo.mercado.TipoPuesto;
import poo.mercado.dao.ClientesDao;
import poo.mercado.dao.ContratosDao;
import poo.mercado.dao.DimensionesDao;
import poo.mercado.dao.EstadosDao;
import poo.mercado.dao.PuestosDao;
import poo.mercado.dao.TiposPuestoDao;

/**
 *
 * @author joaquinleonelrobles
 */
public class GestorAlquilerPuesto {
    
    private PantallaAlquilerDePuesto pantalla;
    
    private final TiposPuestoDao tiposPuestoDao;
    private final DimensionesDao dimensionesDao;
    private final PuestosDao puestosDao;
    private final ClientesDao clientesDao;
    private final ContratosDao contratosDao;
    private final EstadosDao estadosDao;
    
    private final SimpleDateFormat sdf;
    private final Sesion sesion;

    public GestorAlquilerPuesto(TiposPuestoDao tiposPuestoDao, DimensionesDao dimensionesDao, PuestosDao puestosDao, ClientesDao clientesDao, ContratosDao contratosDao, EstadosDao estadosDao, Sesion sesion) {
        this.tiposPuestoDao = tiposPuestoDao;
        this.dimensionesDao = dimensionesDao;
        this.puestosDao = puestosDao;
        this.clientesDao = clientesDao;
        this.contratosDao = contratosDao;
        this.estadosDao = estadosDao;
        this.sesion = sesion;
        
        // creamos un formateador de fechas para poder tomar el ingreso del usuario
        this.sdf = new SimpleDateFormat("dd/MM/yyyy");
    }
    
    public void run () {
        pantalla = new PantallaAlquilerDePuesto(this, sesion.getEmpleado());
        pantalla.setVisible(true);
    }
    
    public List<TipoPuesto> obtenerTiposPuesto () {
        return tiposPuestoDao.obtenerTodos();
    }
    
    public List<Dimension> obtenerDimensiones () {
        return dimensionesDao.obtenerTodas();
    }
    
    public void buscarPuestosDisponibles (String txtFechaInicio, String txtFechaVencimiento, TipoPuesto tipoPuesto, Dimension dimension) {
        // validamos las fechas
        try {
            Date fechaInicio = sdf.parse(txtFechaInicio);
            Date fechaVencimiento = sdf.parse(txtFechaVencimiento);
            
            // obtenemos los puestos disponibles
            List<Puesto> puestos = puestosDao.buscarDisponiblesEnFechas(tipoPuesto, dimension, fechaInicio, fechaVencimiento);
            
            // los mostramos en la tabla
            pantalla.mostrarPuestosDisponibles (puestos);
        }
        catch (ParseException ex) {
            // mostramos la pantalla con el saldo actualizado
            JOptionPane.showMessageDialog(pantalla, "Formato de fechas no válido");
        }
    }

    public void buscarClientePorNombre(String nombre) {
        Cliente cliente = clientesDao.buscarPorNombre(nombre);
        
        if (cliente == null) {
            JOptionPane.showMessageDialog(pantalla, "Cliente no encontrado...");
        }
        
        pantalla.mostrarDatosCliente(cliente);
    }
    
    public void crearContratoAlquiler (Puesto puesto, String txtFechaDesde, String txtFechaHasta, Cliente cliente) {
        try {
            // interpretamos las fechas de vigencia
            Date fechaDesde = sdf.parse(txtFechaDesde);
            Date fechaHasta = sdf.parse(txtFechaHasta);
            
            // consultamos el numero de proximo contrato
            int numeroContrato = contratosDao.obtenerProximoNumero();
            
            // creamos el contrato
            Contrato contrato = cliente.crearContrato (puesto, fechaDesde, fechaHasta, sesion, numeroContrato);
            
            // obtenemos el estado "Alquilado"
            Estado alquilado = estadosDao.buscarPorNombre("Alquilado");
            
            // cambiamos el estado para el puesto
            puesto.alquilar(alquilado);
            
            // guardamos el contrato
            contratosDao.guardar(contrato);
            
            // mostramos el nro de contrato generado
            pantalla.mostrarNumeroDeContrato(contrato);
        }
        catch (ParseException ex) {
            // TODO mostrar mensaje de error de fecha invalida
        }
    }
    
}
