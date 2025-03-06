package manager;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "{\"error\":\"Method Not Allowed\"}";

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", HttpStatusCode.NOT_FOUND.getCode());
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Acceptable\"}", HttpStatusCode.NOT_ACCEPTABLE.getCode());
    }

    protected void sendError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + message + "\"}", HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
    }
}