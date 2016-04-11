package se.nylander.ws.webscraper.exception;

/**
 * Created by erik.nylander on 2016-03-22.
 */
public class JavascriptJsonFormatException extends RuntimeException {

    public JavascriptJsonFormatException(String message){
        super(message);
    }

    public JavascriptJsonFormatException(String message, Exception e){
        super(message, e.getCause());
    }
}
