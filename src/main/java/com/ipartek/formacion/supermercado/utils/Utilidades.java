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
	
	
	/**
	 * Obtenemos el número de palabras de la cadena fase
	 * @param fase, cadena en la que vamos a contar el número de palabras
	 * @return resul, número de palabras de la cadena
	 * 
	 * <p>ejemplos:</p>
	 * <ol>
	 * 		<li>null</li>
	 * 		<li>""</li>
	 * 		<li>" "</li>
	 * 		<li>"hola mundo"</li>
	 * 		<li>"hola   mundo"</li>
	 * 		<li>" hola mundo "</li>
	 * </ol>
	 * */
	public static int contarPalabras(String fase) {
		
		int resul = 0;
		int contador = 0;
		
		if(fase != null) {
			
			if(fase.indexOf("")==-1) {
			
				fase.trim(); //quitamos espacios al principio y al final de la cadena
			
				for(int i=0; i<fase.length();i++){
					if(fase.charAt(i)!=' ') {
						contador++; 
					}
				}
				
				resul = contador;
				
			}else {
				
				String[] partes = fase.split(" ");
				
				for(int i=0; i<partes.length;i++){
					if(partes[i] != "") {
						contador++;
					}
				}
				
				resul = contador;
				
			}
		}
		

		return resul;
	}
	
	
	

}
