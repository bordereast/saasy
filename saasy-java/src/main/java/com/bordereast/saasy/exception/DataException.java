package com.bordereast.saasy.exception;

public class DataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataException(){
		super();
	}
	
	public DataException(String message){
		super(message);
	}
	
	
	public DataException(String message, Exception innerException){
		super(message, innerException);
	}
}
