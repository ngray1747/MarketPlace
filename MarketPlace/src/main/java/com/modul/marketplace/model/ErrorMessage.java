package com.modul.marketplace.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrorMessage implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

//	public  static final  int CODE_POS_NOT_ACTIVE=1405;

	public  static final  int POS_ISNOT_ACTIVE =1601;

	public  static final  int POS_IS_SYNING =1602;

	@SerializedName("message")
	protected String message;

	@SerializedName("code")
	protected String code;

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "ErrorMessage{[message = " + message + ", code=" + code + "}";
	}

}
