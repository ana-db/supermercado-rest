package com.ipartek.formacion.supermercado.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilidadesTest {
/*
	@Test
	public void testObtenerId() throws Exception {
		
		assertEquals(-1, Utilidades.obtenerId(null));
		
		assertEquals(-1, Utilidades.obtenerId("/")); //esperamos recibir un -1 cuando le pasamos Utilidades.obtenerId("/") 
		
		assertEquals(2, Utilidades.obtenerId("/2"));
		assertEquals(2, Utilidades.obtenerId("/2/"));
		assertEquals(99, Utilidades.obtenerId("/99/"));
		
		try {
			assertEquals(-1, Utilidades.obtenerId("/pepe")); //devuelve una excepción porque está mal formado
			fail("Debería haber lanzado Exception"); //si se ejecuta esta línea, es que está mal porque tendría que haber lanzado una excepción
		}catch(Exception e){
			assertTrue(true); //tiene que lanzar una excepción
		}
		
		try {
			assertEquals(-1, Utilidades.obtenerId("/pepe/")); //devuelve una excepción
			fail("Debería haber lanzado Exception"); //si se ejecuta esta línea, es que está mal porque tendría que haber lanzado una excepción
		}catch(Exception e){
			assertTrue(true); //tiene que lanzar una excepción
		}
		
		try {
			assertEquals(99, Utilidades.obtenerId("/99/333/hola/"));
			fail("Debería haber lanzado Exception"); //si se ejecuta esta línea, es que está mal porque tendría que haber lanzado una excepción
		}catch(Exception e){
			assertTrue(true); //tiene que lanzar una excepción
		}
			
		
	}
*/	
	
	
	@Test
	public void contarPalabrasTest1(){
		
		assertEquals(0, Utilidades.contarPalabras(null));
		
		assertEquals(0, Utilidades.contarPalabras(""));
		assertEquals(0, Utilidades.contarPalabras("   "));
		
		assertEquals(2, Utilidades.contarPalabras("hola mundo"));
		assertEquals(2, Utilidades.contarPalabras("hola   mundo"));
		assertEquals(2, Utilidades.contarPalabras(" hola mundo "));
		
	}
	
	
	@Test
	public void contarPalabrasTest2(){
		
		assertEquals(2, Utilidades.contarPalabras("hola,mundo"));
		assertEquals(2, Utilidades.contarPalabras("hola...?mundo"));
		
	}

}
