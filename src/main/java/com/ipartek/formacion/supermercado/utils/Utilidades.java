package com.ipartek.formacion.supermercado.utils;

public class Utilidades {
	
	/**
	 * Obtenemos el id del pathInfo o uri
	 * @param pathInfo, parte de la uri donde debemeos buscar un número
	 * @return numero id
	 * @throws Exception si el path info está mal formado
	 * 
	 * <p>ejemplos:</p>
	 * <ol>
	 * 		<li>/ pathInfo válido</li>
	 * 		<li>/2 pathInfo válido</li>
	 * 		<li>/2/ pathInfo válido</li>
	 * 		<li>/2/2 pathInfo mal formado</li>
	 * 		<li>/2/otracosa/34 pathInfo mal formado</li>
	 * </ol>
	 * */
	public static int obtenerId(String pathInfo) throws Exception {
		
		// throw new Exception("Sin implementar, primero test");
		return -1;
	
	}

}
