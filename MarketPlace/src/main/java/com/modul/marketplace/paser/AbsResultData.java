/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.modul.marketplace.paser;

import com.google.gson.annotations.SerializedName;
import com.modul.marketplace.model.ErrorMessage;


import java.io.Serializable;


/**
 * 
 * @author gianglv3
 */
public class AbsResultData implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	

	
	
	@SerializedName("error")
	private ErrorMessage error;


	public void setError(ErrorMessage error) {
		this.error = error;
	}

	public ErrorMessage getError() {
		return error;
	}


	public boolean isValidLogin() {
//		if (getError()!=null&&getError().getCode()==VALID_TOKEN) {
//			return true;
//		} else if (getError()!=null&&getError().getCode()==ErrorMessage.POS_ISNOT_ACTIVE) {
//			return  true;
//		}
		return false;
	}
}
