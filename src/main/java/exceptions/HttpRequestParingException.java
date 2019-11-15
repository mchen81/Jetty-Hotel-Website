package exceptions;


public class HttpRequestParingException extends RuntimeException {
    private String httpResponse;

    public HttpRequestParingException(String message, int httpResponseCode) {
        super(message);

        switch (httpResponseCode) {
            case 404:
                httpResponse = "HTTP/1.1 404 Page Not Found\n";
                break;
            case 405:
                httpResponse = "HTTP/1.1 405 Page Not Found\n";
                break;
        }


    }

    public String getHttpResponse() {
        return httpResponse;
    }
}
