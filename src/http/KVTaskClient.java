package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private String apiKey;
    private String url;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        this.url = url;
        URI urlReg = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(urlReg).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apiKey = response.body();

    }

    public void save(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/save/" + key + "?apiKey=" + apiKey);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/load/" + key + "?apyKey" + apiKey);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
