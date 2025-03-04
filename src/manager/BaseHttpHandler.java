package manager;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    // Метод для отправки ответа "Not Found" (404)
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", 404);
    }

    // Метод для отправки ответа с ошибкой (500)
    protected void sendError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\":\"" + message + "\"}", 500);
    }

    // Метод для отправки ответа "Not Acceptable" (406)
    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Acceptable\"}", 406);
    }
}