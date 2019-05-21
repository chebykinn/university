package com.ghostflow.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ghostflow.GhostFlowException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;

@Component("commonClient")
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final int MAX_ATTEMPTS = 100_000;
    private static final String AVG_NAME = "timeouts_count";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public HttpClient(OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public OkHttpClient getOkHttpClient() {
        return httpClient;
    }

    public static HttpUrl newHttpUrl(String scheme, String host, String pathSegment, QueryParameters queryParams) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .addPathSegments(pathSegment);
        queryParams.forEach(p -> builder.addQueryParameter(p.getName(), p.getValue()));
        return builder.build();
    }

    public Response execute(Request request) {
        int attempts = 0;
        while (true) {
            try {
                Response response = httpClient.newCall(request).execute();
                return response;
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException && attempts < MAX_ATTEMPTS) {
                    logger.trace("timeout for " + request.url(), e);
                    attempts++;
                } else {
                    throw new GhostFlowException(e);
                }
            }
        }
    }

    public <T> T execute(Request request, Class<T>  clazz) {
        Response response = execute(request);
        try {
            return objectMapper.readValue(response.body().charStream(), clazz);
        } catch (IOException e) {
            throw new GhostFlowException(e);
        } finally {
            response.close();
        }
    }

    public static class QueryParameters {
        private final List<QueryParameter> queryParameters;

        private QueryParameters(QueryParameters queryParameters) {
            this.queryParameters = Lists.newArrayList(queryParameters.queryParameters);
        }

        private QueryParameters(String... o) {
            checkArgument(o.length % 2 == 0, "must be an even number of parameters");
            this.queryParameters = new ArrayList<>(o.length / 2);
            for (int i = 0; i < o.length; i += 2) {
                queryParameters.add(new QueryParameter(o[i], o[i + 1]));
            }
        }

        public static QueryParameters of(String... o) {
            return new QueryParameters(o);
        }

        public void add(String n, String v) {
            queryParameters.add(new QueryParameter(n, v));
        }

        public void forEach(Consumer<? super QueryParameter> action) {
            queryParameters.forEach(action);
        }

        public QueryParameters copy() {
            return new QueryParameters(this);
        }
    }

    public static class QueryParameter {
        private final String name;
        private final String value;

        public QueryParameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
