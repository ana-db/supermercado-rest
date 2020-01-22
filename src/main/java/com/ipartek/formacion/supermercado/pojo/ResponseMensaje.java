package com.ipartek.formacion.supermercado.pojo;

import java.util.ArrayList;

public class ResponseMensaje {
	
	//variables: 
	private String texto;
	private ArrayList<String> errores;
	
	public ResponseMensaje() {
		super();
		this.texto = "";
		this.errores = new ArrayList<String>();
	}
	
	
	//constructores: 
	public ResponseMensaje(String texto) {
		this();
		this.texto = texto;
	}
	
	public ResponseMensaje(String texto, ArrayList<String> errores) {
		this();
		this.texto = texto;
		this.errores = errores;
	}

	
	//getters y setters: 
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public ArrayList<String> getErrores() {
		return errores;
	}

	public void setErrores(ArrayList<String> errores) {
		this.errores = errores;
	}

	
	//toString
	@Override
	public String toString() {
		return "ResponseMensaje [texto=" + texto + ", errores=" + errores + "]";
	}
	
	
	
	
}
