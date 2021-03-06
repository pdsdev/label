package pds.label;

/**
 * PDSException implements an Exception class. Used by other
 * classes to return error information.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 02/17/05
 * @since       1.0
 */
public class PDSException extends Exception {

	private static final long serialVersionUID = -2043324630879333598L;

	public PDSException() { }

	public PDSException(String msg) {
		super(msg);
	}
}
