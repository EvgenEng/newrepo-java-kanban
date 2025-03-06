package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getHistory()), HttpStatusCode.OK.getCode());
                break;
            case "DELETE":
                taskManager.clearHistory();
                sendText(exchange, "", HttpStatusCode.OK.getCode());
                break;
            default:
                sendText(exchange, METHOD_NOT_ALLOWED_MESSAGE, HttpStatusCode.METHOD_NOT_ALLOWED.getCode());
        }
    }
}