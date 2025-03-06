package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import manager.BaseHttpHandler;
import manager.HttpStatusCode;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), HttpStatusCode.OK.getCode());
                    break;
                default:
                    sendText(exchange, METHOD_NOT_ALLOWED_MESSAGE, HttpStatusCode.METHOD_NOT_ALLOWED.getCode());
            }
        } catch (Exception e) {
            sendError(exchange, "Internal Server Error: " + e.getMessage());
        }
    }
}