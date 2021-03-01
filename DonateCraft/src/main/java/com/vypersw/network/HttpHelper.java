package com.vypersw.network;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHelper {

    private final String serverURL;

    public HttpHelper(String serverURL) {
        this.serverURL = serverURL;
    }

    public void fireAsyncPostRequestToServer(String endPoint, JSONObject body, Runnable taskToRunAfterAsyncResponse) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + endPoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        HttpClient client = HttpClient.newBuilder().build();
        if (taskToRunAfterAsyncResponse != null) {
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenRun(taskToRunAfterAsyncResponse);
        } else {
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
        }
    }

    public void fireAsyncPostRequestToServer(String endPoint, JSONObject body) {
        fireAsyncPostRequestToServer(endPoint, body, null);
    }


    public HttpRequest buildGETHttpRequest(String endPoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(serverURL + endPoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }
}
