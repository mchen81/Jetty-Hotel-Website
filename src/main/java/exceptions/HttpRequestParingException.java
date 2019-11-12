package exceptions;

import rawHttpServer.HttpHandler;

public class HttpRequestParingException extends RuntimeException {
    public HttpRequestParingException(String message) {
        super(message);
    }
}
