package io.github.thlws.gateway.client.Interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class NetworkInterceptor implements Interceptor {

    private final static String EMPTY = "";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        String url = request.url().toString();
        String requestHeaders = request.headers().toString();
        String requestBody = getRequestBody(request);
        Response response = chain.proceed(request);

        try {
            String responseHeaders = response.headers().toString();
            String responseBody = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            MediaType mediaType = Objects.requireNonNull(response.body()).contentType();

            log.info("tob-gateway-client ###请求记录### url={},{},requestBody={},responseBody={}",request.method(),url,requestBody,responseBody);

            if (log.isDebugEnabled()) {
                log.debug("tob-gateway-client ###headers###\nrequestHeaders=[{}],\nresponseHeaders=[{}],\nresponseCode={}"
                        ,requestHeaders,responseHeaders,response.code());
            }

            assert mediaType != null;
            return response.newBuilder().body(ResponseBody.create(mediaType,responseBody)).build();
        } catch (Exception e) {
            log.info("tob-gateway-client###拦截请求失败",e);
            return response;
        }
    }


    private String getRequestBody(Request request) {
        try {
            if (Objects.isNull(request)) {
                return EMPTY;
            }
            RequestBody requestBody = request.body();
            if (Objects.isNull(requestBody)) {
                return EMPTY;
            }

            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = StandardCharsets.UTF_8;
            return buffer.readString(charset);
        } catch (Exception e) {
            log.info("日志拦截器处理失败",e);
            return EMPTY;
        }
    }
}
