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
import com.ipartek.formacion.supermercado.pojo.ResponseMensaje;
import com.ipartek.formacion.supermercado.utils.Utilidades;

/**
 * Servlet implementation class ProductoRestController
 */
@WebServlet({ "/producto/*", "/producto"})
public class ProductoRestController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = Logger.getLogger(ProductoRestController.class);
	
	private ProductoDAO productoDao;
	
	private String pathInfo;
	private int statusCode;
	
	
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
		
		//preparamos la respuesta indicando qué tipo de dato devuelve, ContentType y charSet
		//lo hacemos en el service porque son comunes a todos los métodos:
		response.setContentType("application/json"); //por defecto --> text/html;charset=UTF-8
		response.setCharacterEncoding("utf-8");
		
		pathInfo = request.getPathInfo();
		LOG.debug("mirar pathInfo:" + pathInfo + " para saber si es listado o detalle" );

		super.service(request, response); //llama a doGet, doPost, doPut o doDelete
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.trace("peticion GET");
		
		Object responseBody = null;
		
		try {
			
			//buscamos el valor del índice del producto en la url con la función obtenerId:
			int id = Utilidades.obtenerId(pathInfo);
			
			if ( id != -1 ) {	//detalle de un producto según su id
				
				//recuperamos un producto por su id:
				responseBody = productoDao.getById(id);
				
				//response status code:
				if ( null != responseBody ) {
					statusCode = HttpServletResponse.SC_OK;	//200, ok
				}else {
					statusCode = HttpServletResponse.SC_NOT_FOUND;	//404, no se encuentra el recurso solicitado
				}
				
			}else {		//listado de todos los productos de la bd
				
				//recuperamos todos los productos de la bd:
				responseBody = (ArrayList<Producto>) productoDao.getAll();
				
				//response status code:
				if (  ((ArrayList<Producto>)responseBody).isEmpty()  ) {
					statusCode = HttpServletResponse.SC_NO_CONTENT;	//204, no hay contenido: encuentra el recurso pero está vacío
				}else {
					statusCode = HttpServletResponse.SC_OK;	//200, ok
				}
				
			}			
			
		}catch (Exception e) {			
			// response status code
			responseBody = new ResponseMensaje(e.getMessage());			
			statusCode = HttpServletResponse.SC_BAD_REQUEST;
			
		} finally  {
			response.setStatus( statusCode );
			
			//response body (lo ponemos en el finally porque lo utilizamos para listar todos los productos y para el detalle)
			PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
			String jsonResponseBody = new Gson().toJson(responseBody); //convertir java -> json (usando la librería gson)
			out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
			out.flush(); //termina de escribir los datos en el body      
		}	
		
		
		//con el código anterior, hemos mejorado el siguiente usando la función obtenerId para controlar errores en la uri e intentando no repetir código
		/*
		if (pathInfo == null || "/".equals(pathInfo)) {
			//recuperamos todos los productos de la bd:
			ArrayList<Producto> lista = (ArrayList<Producto>) productoDao.getAll();
			
			//response body
			PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
			String jsonResponseBody = new Gson().toJson(lista); //convertir java -> json (usando la librería gson)
			out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
			out.flush(); //termina de escribir los datos en el body
			
			//response status code:
			if ( !lista.isEmpty() ) {			
				response.setStatus( HttpServletResponse.SC_OK ); //200, ok
			}else {
				response.setStatus( HttpServletResponse.SC_NO_CONTENT ); //204, no hay contenido: encuentra el recurso pero está vacío
			}
		}else {
			//buscamos el valor del índice del producto en la url
			String partes[] = pathInfo.split("/"); //pathInfo --> / ó /8
			String parte1 = partes[0]; // nos devolverá lo que haya a la izqda de /, (nada)
			String parte2 = partes[1]; // nos devolverá lo que haya a la drcha de /, que en caso de haber algo, será el id
				
			int id = (parte2.isEmpty()) ? 0 : Integer.parseInt(parte2); //int id = Integer.parseInt(parte2);
			
			if(id != 0) {
				//recuperamos un producto por su id:
				Producto productoVisualizar = new Producto();
				productoVisualizar = productoDao.getById(id);
				
				//response body
				PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
				String jsonResponseBody = new Gson().toJson(productoVisualizar); //convertir java -> json (usando la librería gson)
				out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
				out.flush(); //termina de escribir los datos en el body
				
				//response status code:
				if ( productoVisualizar != null ) {			
					response.setStatus( HttpServletResponse.SC_OK ); //200, ok
				}else {
					response.setStatus( HttpServletResponse.SC_NOT_FOUND ); //404, no se encuentra el recurso solicitado
				}
				
			}
		}
		*/

		
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
			response.setStatus( HttpServletResponse.SC_CREATED ); //201, creado
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////////////////////////////////////////
		
		// convertir json del request body a Objeto
		BufferedReader reader = request.getReader(); //coge los datos del body de postman           
		Gson gson = new Gson(); //crea un objeto gson
		Producto producto = gson.fromJson(reader, Producto.class); //convierte el objeto gson en uno de la clase Producto
		
		//TODO validar objeto bien creado
		
		LOG.debug(" Json convertido a Objeto: " + producto);
		
		//response body para enviar posibles errores:
		PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
		String jsonResponseBody = new Gson().toJson(nuevoProducto); //convertir java -> json (usando la librería gson)
		out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
		out.flush(); //termina de escribir los datos en el body
		
	}
	

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.debug("PUT modificar recurso");
		
		response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOG.debug("DELETE eliminar recurso");
		
		response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
	}

}
