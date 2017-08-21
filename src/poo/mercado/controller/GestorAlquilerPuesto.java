/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.mercado.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.SessionFactory;
import poo.mercado.Cliente;
import poo.mercado.Contrato;
import poo.mercado.Dimension;
import poo.mercado.Estado;
import poo.mercado.Puesto;
import poo.mercado.Sesion;
import poo.mercado.TipoPuesto;
import poo.mercado.dao.ClientesDao;
import poo.mercado.dao.ClientesDaoHibernateImpl;
import poo.mercado.dao.ContratosDao;
import poo.mercado.dao.ContratosDaoHibernateImpl;
import poo.mercado.dao.DimensionesDao;
import poo.mercado.dao.DimensionesDaoHibernateImpl;
import poo.mercado.dao.EstadosDao;
import poo.mercado.dao.EstadosDaoHibernateImpl;
import poo.mercado.dao.PuestosDao;
import poo.mercado.dao.PuestosDaoHibernateImpl;
import poo.mercado.dao.TiposPuestoDao;
import poo.mercado.dao.TiposPuestoDaoHibernateImpl;
import poo.mercado.ui.PantallaAlquilerDePuesto;

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
    private final SessionFactory sessionFactory;
    
    private Date fechaDesde, fechaHasta;

    public GestorAlquilerPuesto(SessionFactory sessionFactory, Sesion sesion) {
        this.sessionFactory = sessionFactory;
        this.sesion = sesion;
        
        // creamos las intancias de la capa DAO
        this.tiposPuestoDao = new TiposPuestoDaoHibernateImpl(sessionFactory);
        this.dimensionesDao = new DimensionesDaoHibernateImpl(sessionFactory);
        this.estadosDao = new EstadosDaoHibernateImpl(sessionFactory);
        this.puestosDao = new PuestosDaoHibernateImpl(sessionFactory, estadosDao);
        this.clientesDao = new ClientesDaoHibernateImpl(sessionFactory);
        this.contratosDao = new ContratosDaoHibernateImpl(sessionFactory);
        
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
            fechaDesde = sdf.parse(txtFechaInicio);
            fechaHasta = sdf.parse(txtFechaVencimiento);
            
            // obtenemos los puestos disponibles
            List<Puesto> puestos = puestosDao.buscarDisponiblesEnFechas(tipoPuesto, dimension, fechaDesde, fechaHasta);
            
            // los mostramos en la tabla
            pantalla.mostrarPuestosDisponibles (puestos);
        }
        catch (ParseException ex) {
            // mostramos la pantalla con el saldo actualizado
            JOptionPane.showMessageDialog(pantalla, "Formato de fechas no válido");
        }
    }

    public void buscarClientePorNombre(String nombre) {
        Cliente cliente = clientesDao.buscarPorRazonSocial(nombre);
        
        if (cliente == null) {
            JOptionPane.showMessageDialog(pantalla, "Cliente no encontrado...");
        }
        
        pantalla.mostrarDatosCliente(cliente);
    }
    
    public void crearContratoAlquiler (Puesto puesto, Cliente cliente) {
        // consultamos el numero de proximo contrato
        int numeroContrato = contratosDao.obtenerProximoNumero();

        // creamos el contrato
        Contrato contrato = cliente.crearContrato (puesto, fechaDesde, fechaHasta, sesion, numeroContrato);

        // obtenemos el estado "Alquilado"
        Estado alquilado = estadosDao.buscarPorNombre("Alquilado");

        // cambiamos el estado para el puesto
        puesto.alquilar(alquilado);
        
        // guardamos el puesto
        puestosDao.guardar(puesto);

        // guardamos el contrato
        clientesDao.guardar(cliente);

        // mostramos el nro de contrato generado
        pantalla.mostrarNumeroDeContrato(contrato);
    }

    public void iniciarGenerarReporte() {
        new GestorReporte(sessionFactory).run();
    }
    
}
