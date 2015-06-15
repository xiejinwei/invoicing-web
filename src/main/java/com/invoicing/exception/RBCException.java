package com.invoicing.exception;

public class RBCException extends RuntimeException {

	private static final long serialVersionUID = 1136644323769074429L;

	private String msg;

	@Override
	public String getMessage() {
		return getMsg();
	}

	public RBCException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
