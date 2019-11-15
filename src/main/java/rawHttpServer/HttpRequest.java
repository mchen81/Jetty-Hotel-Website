package rawHttpServer;

import exceptions.HttpRequestParingException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that represents an http request
 */
public class HttpRequest {
    // FILL IN CODE
    // Store parameters of the request
    /**
     * A String contains all http request: GET /action?parameters HTTP/1.1
     */
    private String httpRequest;
    /**
     * A String contains request: action?parameters
     */
    private String urlRequest;
    /**
     * key as parameter's name, value is the value of parameter
     */
    private Map<String, String> parameterMap;
    /**
     * represent the query action: hotelInfo, reviews, attractions
     */
    private String action = null;

    /**
     * HTTP's CRUD : GET, POST ....
     */
    private String httpCRUD = null;

    /**
     * @param httpRequest A String like: GET /action?parameters HTTP/1.1
     */
    public HttpRequest(String httpRequest) {
        this.httpRequest = httpRequest;
        parameterMap = new HashMap<>();
        parseHttpRequest();
    }

    /**
     * parse httpRequest and store the elements in this class
     */
    private void parseHttpRequest() {
        String[] request = httpRequest.split(" ");
        if (request.length == 3) {
            httpCRUD = request[0];
            urlRequest = request[1];
            parseUrlRequest();
        } else {
            throw new HttpRequestParingException("Cannot Parse HTTP Request: " + httpRequest, 404);
        }

    }

    private void parseUrlRequest() {
        String[] query = urlRequest.split("\\?");
        if (query.length == 2) {
            action = query[0].replace("/", "");
            String parameters = query[1];
            parseParametersToMap(parameters);
        } else if (query.length == 1) {
            action = query[0].replace("/", "");
        } else {
            throw new HttpRequestParingException("Cannot Parse query request: " + urlRequest, 405);
        }

    }

    private void parseParametersToMap(String parameters) {
        String[] parameterArray = parameters.split("&");
        for (String parameter : parameterArray) {
            String[] map = parameter.split("=");
            try {
                parameterMap.put(map[0], map[1]);
            } catch (IndexOutOfBoundsException e) {
                throw new HttpRequestParingException("Cannot parse parameters: " + parameters, 405);
            }

        }
    }


    public String getValue(String parameterKey) {
        if (parameterMap.containsKey(parameterKey)) {
            return parameterMap.get(parameterKey);
        }
        return "";
    }

    public boolean containsKey(String parameterKey) {
        return parameterMap.containsKey(parameterKey);
    }

    public String getAction() {
        return null != action ? action : "";
    }

    public String getHttpCRUD() {
        return null != httpCRUD ? httpCRUD : "";
    }
}

