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
    private String httpRequest;

    private String urlRequest;

    private Map<String, String> parameterMap;

    private String action = null;

    private String httpCRUD = null;

    public HttpRequest(String httpRequest) {
        this.httpRequest = httpRequest;
        parameterMap = new HashMap<>();
        parseHttpRequest();
    }

    private void parseHttpRequest() {
        Pattern requestPattern = Pattern.compile("([A-Za-z]*?) (.*) (\\/?HTTP\\/1.1.*)");
        Matcher requestMatcher = requestPattern.matcher(httpRequest);

        if (requestMatcher.find()) {
            httpCRUD = requestMatcher.group(1);
            urlRequest = requestMatcher.group(2);
            parseUrlRequest();
        } else {
            throw new HttpRequestParingException("Cannot Parse HTTP Request" + httpRequest);
        }

    }

    private void parseUrlRequest() {
        String urlRegex = "(.*?)\\?(.*)";
        Pattern urlPattern = Pattern.compile(urlRegex);
        Matcher urlMatcher = urlPattern.matcher(urlRequest);

        if (urlMatcher.find()) {
            action = urlMatcher.group(1);
            String parameters = urlMatcher.group(2);
            parseParametersToMap(parameters);
        } else {
            throw new HttpRequestParingException("Cannot Parse Action");
        }

    }

    private void parseParametersToMap(String parameters) {
        String[] parameterArray = parameters.split("&");
        for (String parameter : parameterArray) {
            String[] map = parameter.split("=");
            try {
                parameterMap.put(map[0], map[1]);
            } catch (IndexOutOfBoundsException e) {
                throw new HttpRequestParingException("Cannot parse query request");
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

