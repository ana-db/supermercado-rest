package com.ipartek.formacion.supermercado.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.supermercado.modelo.dao.ProductoDAO;
import com.ipartek.formacion.supermercado.modelo.pojo.Producto;

/**
 * Servlet implementation class ProductoRestController
 */
@WebServlet({ "/producto/*", "/producto" })
public class ProductoRestController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = Logger.getLogger(ProductoRestController.class);
	
	private ProductoDAO productoDao;
	
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		productoDao = ProductoDAO.getInstance();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		productoDao = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		super.service(request, response); //llama a doGet, doPost, doPut o doDelete
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.trace("peticion GET");
		
		String pathInfo = request.getPathInfo();
		
		LOG.debug("mirar pathInfo:" + pathInfo + " para saber si es listado o detalle" );
		
		//recuperamos todos los productos de la bd:
		ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();
		
		//preparamos la respuesta indicando qué tipo de dato devuelve:
		response.setContentType("application/json"); //por defecto --> text/html;charset=UTF-8
		response.setCharacterEncoding("utf-8");
		
		//response body
		PrintWriter out = response.getWriter(); //se encarga de poder escribir los datos en el body
		String jsonResponseBody = new Gson().toJson(lista); //convertir java -> json (usando la librería gson)
		out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
		out.flush(); //termina de escribir los datos en el body
		
		//response status code:
		if ( !lista.isEmpty() ) {			
			response.setStatus( HttpServletResponse.SC_OK ); //200
		}else {
			response.setStatus( HttpServletResponse.SC_NO_CONTENT ); //204
		}	
		
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.debug("POST crear recurso");
		
		/////////////////////////////////////////////
		Producto nuevoProducto = null;
		
		try {
			productoDao.create(nuevoProducto);
			response.setStatus( HttpServletResponse.SC_OK ); //200
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////////////////////////////////////////
		
		// convertir json del request body a Objeto
		BufferedReader reader = request.getReader();               
		Gson gson = new Gson();
		Producto producto = gson.fromJson(reader, Producto.class);
		
		LOG.debug(" Json convertido a Objeto: " + producto);
		
		response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
	}
	

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
