package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8081;
    private final String apiKey;
    private HttpServer server;
    private Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiKey = generateApiKey();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/register", (h) -> {
            register(h);
        });

        server.createContext("/save", (h) -> {
            post(h);
        });

        server.createContext("/load", (h) -> {
            get(h);
        });
    }

    private void register(HttpExchange h) {
        try {
            System.out.println("\n/register");
            switch (h.getRequestMethod()) {
                case "GET":
                    sendText(h, apiKey);
                    break;
                default:
                    System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }

    }

    private void post(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query apiKey со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            switch (h.getRequestMethod()) {
                case "POST":
                    String key = h.getRequestURI().getPath().substring("/save/".length());

                    if (key.isEmpty()) {
                        System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    String value = readText(h);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    data.put(key, value);
                    System.out.println("Значение для ключа " + key + " успешно обновлено!");
                    h.sendResponseHeaders(200, 0);
                    break;
                default:
                    System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void get(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            switch (h.getRequestMethod()) {
                case "GET":
                    String key = h.getRequestURI().getPath().substring("/load/".length());
                    if (key.isEmpty()) {
                        System.out.println("key для загрузки пустой");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    sendText(h, data.get(key));
                    break;
                default:
                    System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("apiKey: " + apiKey);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String generateApiKey() {
        return String.valueOf(System.currentTimeMillis());
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("apiKey=" + apiKey) || rawQuery.contains("apiKey=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), "UTF-8");
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes("UTF-8");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
