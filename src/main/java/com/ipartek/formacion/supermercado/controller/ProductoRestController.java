package com.ipartek.formacion.supermercado.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.supermercado.modelo.dao.ProductoDAO;
import com.ipartek.formacion.supermercado.modelo.pojo.Producto;
import com.ipartek.formacion.supermercado.pojo.ResponseMensaje;
import com.ipartek.formacion.supermercado.utils.Utilidades;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

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
	private Object responseBody;
	
	private int id;
	
	//Crear Factoria y Validador
	ValidatorFactory factory;
	Validator validator;
	
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		productoDao = ProductoDAO.getInstance();
		
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		
		productoDao = null;
		
		factory = null;
		validator = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.debug( "llamada al método " + request.getMethod() + " - URL " + request.getRequestURL() + " - URI " + request.getRequestURI() );
		
		//preparamos la respuesta indicando qué tipo de dato devuelve, ContentType y charSet
		//lo hacemos en el service porque son comunes a todos los métodos:
		response.setContentType("application/json"); //por defecto --> text/html;charset=UTF-8
		response.setCharacterEncoding("utf-8");
		
		responseBody = null;
		
		pathInfo = request.getPathInfo();
		LOG.debug("mirar pathInfo:" + pathInfo + " para saber si es listado o detalle" );
		
		
		//el siguiente código es común a varias funciones GET, POST, PUT y DELETE, por eso lo escribimos en el service:
		try { 
			
			//buscamos el valor del índice del producto en la url con la función obtenerId:
			//(lo hacemmos en el service porque lo vamos a necesitar para varios métodos)
			id = Utilidades.obtenerId(pathInfo);
			
			super.service(request, response); //llama a doGet, doPost, doPut o doDelete  
			
		}catch (Exception e) {
			
			statusCode = HttpServletResponse.SC_BAD_REQUEST; //400, error del cliente: solicitud mal formada, sintaxis errónea...
			responseBody = new ResponseMensaje(e.getMessage());		
					
		}finally {	
			
			response.setStatus( statusCode );
			
			if ( responseBody != null ) { 
				
				//response body (lo ponemos en el finally porque lo utilizamos para listar todos los productos y para el detalle)
				PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
				String jsonResponseBody = new Gson().toJson(responseBody); //convertir java -> json (usando la librería gson)
				out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
				out.flush(); //termina de escribir los datos en el body      
			}	
		}	
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LOG.trace("peticion GET");
		
		//cogemos el valor del índice del producto en la url con la función obtenerId (lo hacemos en el service, int id = Utilidades.obtenerId(pathInfo); )
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
			/*
			//recuperamos todos los productos de la bd ordenados por nombre:
			responseBody = (ArrayList<Producto>) productoDao.getAllOrdenado(request.getParameter("_orden"));
			*/
			//response status code:
			if (  ((ArrayList<Producto>)responseBody).isEmpty()  ) {
				statusCode = HttpServletResponse.SC_NO_CONTENT;	//204, no hay contenido: encuentra el recurso pero está vacío
			}else {
				statusCode = HttpServletResponse.SC_OK;	//200, ok
			}
			
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
		
		doPut(request, response);
		
		/*
		LOG.debug("POST crear recurso");
		
		try {
		
			// convertir json del request body a Objeto:
			BufferedReader reader = request.getReader(); //coge los datos del body de postman           
			Gson gson = new Gson(); //crea un objeto gson
			Producto producto = gson.fromJson(reader, Producto.class);	//convierte el objeto gson en uno de la clase Producto
			LOG.debug(" Json convertido a Objeto: " + producto);
		
			//validamos que el objeto esté bien creado:
			Set<ConstraintViolation<Producto>>  validacionesErrores = validator.validate(producto);		
			if ( validacionesErrores.isEmpty() ) {
		
				Producto pNuevo = productoDao.create(producto);
				
				//response status code:
				statusCode = HttpServletResponse.SC_CREATED;	//201, creado 
				responseBody = pNuevo;
				
			}else {
				
				//response status code:			
				statusCode = HttpServletResponse.SC_BAD_REQUEST;	//400, datos incorrectos para un producto: precio negativo…
				ResponseMensaje responseMensaje = new ResponseMensaje("Los valores de este producto no son correctos, revisalos por favor");
				
				//enviamos un array de errores para que el usuario tenga una idea de qué datos ha metido mal y por qué:
				ArrayList<String> errores = new ArrayList<String>();
				for (ConstraintViolation<Producto> error : validacionesErrores) {					 
					errores.add( error.getPropertyPath() + " " + error.getMessage() );
				}				
				responseMensaje.setErrores(errores);				
				responseBody = responseMensaje;
				
			}
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			// response status code
			responseBody = new ResponseMensaje("El nombre del producto ya existe en la base de datos, elige otro");			
			statusCode = HttpServletResponse.SC_CONFLICT;	//409, nombre duplicado en la bd
		} catch (Exception e) {
			// response status code
			responseBody = new ResponseMensaje(e.getMessage());			
			statusCode = HttpServletResponse.SC_BAD_REQUEST;	//400, datos incorrectos para un producto: precio negativo…
		} 
		*/
		
	}
	

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		try {
			
			//cogemos el valor del índice del producto en la url con la función obtenerId (lo hacemos en el service, int id = Utilidades.obtenerId(pathInfo); )
			
	//		if ( id != -1 ) { //significa que el producto sí existe en la bd. Lo editamos según su id
				
				// convertir json del request body a Objeto:
				BufferedReader reader = request.getReader(); //coge los datos del body de postman           
				Gson gson = new Gson(); //crea un objeto gson
				Producto producto = gson.fromJson(reader, Producto.class);	//convierte el objeto gson en uno de la clase Producto
				LOG.debug(" Json convertido a Objeto: " + producto);
			
				//validamos que el objeto esté bien creado:
				Set<ConstraintViolation<Producto>>  validacionesErrores = validator.validate(producto);		
				if ( validacionesErrores.isEmpty() ) {
					
					if ( id != -1 ) { //significa que el producto sí existe en la bd. Lo editamos según su id
						
						LOG.debug("PUT modificar recurso");
						
						Producto pEditar = productoDao.update(producto, id);
						
						//response status code:
						statusCode = HttpServletResponse.SC_OK;	//200, ok
						responseBody = pEditar;
				
					}else { //id == -1: hemos entrado por doPost, significa que el producto no existe en la bd, así que lo creamos
						
						LOG.debug("POST crear recurso");
						
						Producto pNuevo = productoDao.create(producto);
						
						//response status code:
						statusCode = HttpServletResponse.SC_CREATED;	//201, creado 
						responseBody = pNuevo;
					}
					
				}else {
					
					//response status code:			
					statusCode = HttpServletResponse.SC_BAD_REQUEST;	//400, datos incorrectos para un producto: precio negativo…
					ResponseMensaje responseMensaje = new ResponseMensaje("Los valores de este producto no son correctos, revisalos por favor");
					
					//enviamos un array de errores para que el usuario tenga una idea de qué datos ha metido mal y por qué:
					ArrayList<String> errores = new ArrayList<String>();
					for (ConstraintViolation<Producto> error : validacionesErrores) {					 
						errores.add( error.getPropertyPath() + " " + error.getMessage() );
					}				
					responseMensaje.setErrores(errores);				
					responseBody = responseMensaje;
					
				}
				
		} catch (MySQLIntegrityConstraintViolationException e) {
			// response status code
			responseBody = new ResponseMensaje("El nombre del producto ya existe en la base de datos, elige otro");			
			statusCode = HttpServletResponse.SC_CONFLICT;	//409, nombre duplicado en la bd
		} catch (Exception e) {
			// response status code
			responseBody = new ResponseMensaje(e.getMessage());			
			statusCode = HttpServletResponse.SC_BAD_REQUEST;	//400, datos incorrectos para un producto: precio negativo…
		} 
		
		
/*		
		// convertir json del request body a Objeto
		BufferedReader reader = request.getReader(); //coge los datos del body de postman           
		Gson gson = new Gson(); //crea un objeto gson
		Producto producto = gson.fromJson(reader, Producto.class);	//convierte el objeto gson en uno de la clase Producto
		
		//buscamos el valor del índice del producto en la url con la función obtenerId:
		int id;
		try {
			
			id = Utilidades.obtenerId(pathInfo);

			responseBody = productoDao.update(producto, id);
			
			//response status code:
			if ( responseBody != null ) {
				statusCode = HttpServletResponse.SC_OK;	//200, ok
			}else {
				statusCode = HttpServletResponse.SC_NOT_FOUND;	//404, no se encuentra el recurso solicitado
			}
			
		} catch (Exception e) {
			// response status code
			responseBody = new ResponseMensaje(e.getMessage());			
			statusCode = HttpServletResponse.SC_BAD_REQUEST;	//400, datos incorrectos para un producto: precio negativo…
			
		} finally  {
			response.setStatus( statusCode );
			
			LOG.debug(" Json convertido a Objeto: " + producto);
			
			//response body para enviar posibles errores:
			PrintWriter out = response.getWriter(); //se encarga de escribir los datos en el body de la response
			String jsonResponseBody = new Gson().toJson(responseBody); //convertir java -> json (usando la librería gson)
			out.print(jsonResponseBody.toString()); //retornamos un array vacío en json dentro del body
			out.flush(); //termina de escribir los datos en el body      
		}	*/
	}

	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOG.debug("DELETE eliminar recurso");
		
		//cogemos el valor del índice del producto en la url con la función obtenerId (lo hacemos en el service, int id = Utilidades.obtenerId(pathInfo); )
			
		if ( id != -1 ) {	//significa que el producto sí existe en la bd. Lo eliminamos por su id
				
			try {
				
				Producto pEliminar = productoDao.delete(id);
				responseBody = pEliminar;
				
				//response status code:
				statusCode = HttpServletResponse.SC_OK;	//200, ok
				
			} catch (Exception e) {

				//response status code:
				statusCode = HttpServletResponse.SC_NOT_FOUND;	//404, no se encuentra el recurso solicitado
				responseBody = new ResponseMensaje(e.getMessage());
			}

		}		
			
	}
	
	

}
