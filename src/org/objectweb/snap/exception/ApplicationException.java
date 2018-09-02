package org.objectweb.snap.exception;

public class ApplicationException extends Exception {
	
	
  public ApplicationException (String msg) {
    super (msg);
  }

  public ApplicationException(Exception e) {	
	super (e);
  }
}
