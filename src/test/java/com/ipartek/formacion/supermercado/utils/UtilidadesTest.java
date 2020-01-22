package com.ipartek.formacion.supermercado.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilidadesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testObtenerId() throws Exception {
		
		assertEquals(-1, Utilidades.obtenerId(null));
		
		assertEquals(-1, Utilidades.obtenerId("/")); //esperamos recibir un -1 cuando le pasamos Utilidades.obtenerId("/") 
		assertEquals(-1, Utilidades.obtenerId("/pepe"));
		assertEquals(-1, Utilidades.obtenerId("/pepe/"));
		assertEquals(2, Utilidades.obtenerId("/2"));
		assertEquals(2, Utilidades.obtenerId("/2/"));
		assertEquals(99, Utilidades.obtenerId("/99/"));
		
		try {
			assertEquals(99, Utilidades.obtenerId("/99/333/hola/"));
			fail("Debería haber lanzado Exception"); //si se ejecuta esta línea, es que está mal porque tendría que haber lanzado una excepción
		}catch(Exception e){
			assertTrue(true); //tiene que lanzar una excepción
		}
		
	}

}
