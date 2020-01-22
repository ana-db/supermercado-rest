package com.ipartek.formacion.supermercado.utils;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.ipartek.formacion.supermercado.modelo.pojo.Producto;

public class Utilidades {
	
	/**
	 * Obtenemos el id del pathInfo o uri
	 * @param pathInfo, parte de la uri donde debemeos buscar un número
	 * @return numero id
	 * @throws Exception si el pathInfo está mal formado
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
		
		int id = -1;
		
		if (pathInfo != null ) {
			
			//buscamos el valor del índice del producto en la url
			String[] partes = pathInfo.split("/"); 
			if(partes.length == 2) {
				id = Integer.parseInt(partes[1]); 
			}else if(partes.length > 2) {
				throw new Exception("El pathInfo está mal formado" + pathInfo);
			}
				
		}

		return id;
	
	}

}
