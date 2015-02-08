package com.kamomileware.define.model.term;

import org.junit.Test;

/**
 * Created by pepe on 19/07/14.
 */
public class FormatterTest {

    @Test
    public void TestNormalFormatter(){
        //System.out.println(Formatters.normalFormatter.format(null));
        System.out.println(Formatters.normalFormatter.format(""));
        System.out.println(Formatters.normalFormatter.format("cadena unica"));
        System.out.println(Formatters.normalFormatter.format("cadena unica."));
        System.out.println(Formatters.normalFormatter.format("varias cadenas.una detras de otra."));
    }

    @Test
    public void TestSiglasFormatter(){
        System.out.println(Formatters.siglasFormatter.format("esto es una prueba de formateo de siglas"));
        System.out.println(Formatters.siglasFormatter.format("a"));
        System.out.println(Formatters.siglasFormatter.format(""));
    }
}
