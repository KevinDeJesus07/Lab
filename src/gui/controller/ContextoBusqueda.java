/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.controller;

/**
 *
 * @author Kevin
 */
public class ContextoBusqueda {

    public String tipoBusqueda;
    public Integer año;
    public Double valor;

    // Constantes para tipos de búsqueda
    public static final String ANUAL = "ANUAL";           // A: Temp(año) > Promedio(año)
    public static final String ANUAL_VS_GLOBAL = "ANUAL_VS_GLOBAL"; // B: Temp(año) < Promedio(global)
    public static final String VALOR = "VALOR";           // C: FM ≥ valor

    /**
     * Constructor completo
     */
    public ContextoBusqueda(String tipo, Integer año, Double valor) {
        this.tipoBusqueda = tipo;
        this.año = año;
        this.valor = valor;
    }

    // Getters
    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public Integer getAño() {
        return año;
    }

    public Double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "ContextoBusqueda{tipo='" + tipoBusqueda + "', año=" + año + ", valor=" + valor + "}";
    }
}
