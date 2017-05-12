/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

/**
 *
 * @author lordminx
 */
public class BadWorldException extends Exception {
    
    public BadWorldException()
		{
		}

    public BadWorldException(String message)
    {
            super(message);
    }

    public BadWorldException(Throwable cause)
    {
            super(cause);
    }

    public BadWorldException(String message, Throwable cause)
    {
            super(message, cause);
    }

    public BadWorldException(String message, Throwable cause, 
                               boolean enableSuppression, boolean writableStackTrace)
    {
            super(message, cause, enableSuppression, writableStackTrace);
    }
    
    
    
}
