package com.vypersw.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class HttpHelper {

    private final String serverURL;

    public HttpHelper(String serverURL) {
        this.serverURL = serverURL;
    }

    public void fireAsyncPostRequestToServer(String endPoint, Object objectToMap, Runnable taskToRunAfterAsyncResponse) {
        String objectJSON = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            objectJSON = objectMapper.writeValueAsString(objectToMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverURL + endPoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectJSON))
                    .build();
            System.out.println(serverURL + endPoint);
            HttpClient client = HttpClient.newBuilder().build();
            if (taskToRunAfterAsyncResponse != null) {
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenRun(taskToRunAfterAsyncResponse);
            } else {
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void fireAsyncPostRequestToServer(String endPoint, Object objectToMap) {
        fireAsyncPostRequestToServer(endPoint, objectToMap, null);
    }


    public HttpRequest buildGETHttpRequest(String endPoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(serverURL + endPoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    public void fireAsyncGetRequestToServer(String endPoint, Function<HttpResponse<String>, Void> f) {
        HttpRequest request = this.buildGETHttpRequest(endPoint);
        HttpClient client = HttpClient.newBuilder().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(f);
    }
}
