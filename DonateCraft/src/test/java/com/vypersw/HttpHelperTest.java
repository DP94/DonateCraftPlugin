package com.vypersw;

import com.vypersw.network.HttpHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HttpHelperTest {

    @Test
    public void testThatBuildGetHttpRequestUsesApplicationJSONAsContentType() {
        HttpHelper httpHelper = new HttpHelper("http://localhost");
        HttpRequest request = httpHelper.buildGETHttpRequest("test");
        Map<String, List<String>> headers = request.headers().map();
        List<String> values = headers.get("Content-Type");
        assertThat(values, contains("application/json"));
    }

    @Test
    public void testThatBuildGetHttpRequestUsesGETMethod() {
        HttpHelper httpHelper = new HttpHelper("http://localhost");
        HttpRequest request = httpHelper.buildGETHttpRequest("test");
        assertEquals("GET", request.method());
    }

    @Test
    public void testThatBuildGetHttpRequestUsesCorrectURIPassedIn() {
        HttpHelper httpHelper = new HttpHelper("http://localhost");
        HttpRequest request = httpHelper.buildGETHttpRequest("/test");
        assertEquals("http://localhost/test", request.uri().toString());
    }
}
