package com.bl.addbookcsvjson;

public class AddressBookException extends Exception {
	public enum ExceptionType {
		 FILE_WRITE_ERROR, FILE_READ_ERROR,
		SQL_QUERY_EXECUTION_ERROR,
		CONNECTION_CREATE_ERROR, CONNECTION_CLOSING_ERROR
    }

    public ExceptionType type;

    public AddressBookException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public AddressBookException(String message, ExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
}
