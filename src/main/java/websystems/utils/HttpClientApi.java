/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websystems.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.extern.log4j.Log4j2;
import okhttp3.*;

/**
 * @author Murad
 */
@Log4j2
public class HttpClientApi {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String HTTP_API_URI = "https://192.168.34.5:8443/api/v1/Mdm/SaveV2";
    private static final String TOKEN_REQUEST_URI = "https://192.168.34.5:8443/Token";
    private static final String HTTP_API_LOGIN = "qsystem@mail.ru";//subsidi_jku@mail.ru
    private static final String HTTP_API_PASSWORD = "Qsystem!2020";//subsidi.jku

    private static String TOKEN;
    private static String pass;


    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static final SSLContext TRUST_ALL_SSL_CONTEXT;

    static {
        try {
            TRUST_ALL_SSL_CONTEXT = SSLContext.getInstance("SSL");
            TRUST_ALL_SSL_CONTEXT.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SSLSocketFactory TRUST_ALL_SSL_SOCKET_FACTORY = TRUST_ALL_SSL_CONTEXT.getSocketFactory();

    public static OkHttpClient trustAllSslClient(OkHttpClient client) {

        OkHttpClient.Builder builder = client.newBuilder();
        builder.sslSocketFactory(TRUST_ALL_SSL_SOCKET_FACTORY, (X509TrustManager) TRUST_ALL_CERTS[0]);
        builder.hostnameVerifier((String hostname, SSLSession session) -> true);
        return builder.build();
    }

    public static void main(String[] args) {
        OkHttpClient client = HttpClientApi.trustAllSslClient(new OkHttpClient());
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("grant_type=").append("password")
                .append("&username=").append(HTTP_API_LOGIN)
                .append("&password=").append(HTTP_API_PASSWORD);

        RequestBody body = RequestBody.create(JSON, strBuild.toString());

        HttpUrl urlBuilder = HttpUrl.get(TOKEN_REQUEST_URI);

        Request request = new Request.Builder()
                .url(urlBuilder)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream()));
                StringBuilder buffer = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    buffer.append(line);
                }
                Gson gson = new Gson();
                gson.toJson(buffer.toString());
                JsonObject jsonObject = gson.fromJson(buffer.toString(), JsonObject.class);
                jsonObject.get("access_token"); // returns a JsonElement for that name
                TOKEN = jsonObject.get("access_token").getAsString();
                System.out.println("Token: " + TOKEN);
            } else {
                log.error("Ошибка: " + response.code() + " " + response.message());
                System.out.println("Error: " + response.code() + " " + response.message());
            }

        } catch (IOException e) {
            log.error("Ошибка при попытке выполнить Http запрос", e);
        }

    }

    /**
     * Метод получения токена.
     */
    public static void getToken() {
        log.debug("[QWebClient] Get the token");
        OkHttpClient client = HttpClientApi.trustAllSslClient(new OkHttpClient());

        String strBuild = "grant_type=" + "password" +
                "&username=" + AppSettings.get("username").toString() +
                "&password=" + AppSettings.get("password").toString();
        RequestBody body = RequestBody.create(JSON, strBuild);
        String tokenUri = AppSettings.get("tokenUri").toString();
        HttpUrl urlBuilder = HttpUrl.get(tokenUri);

        Request request = new Request.Builder()
                .url(urlBuilder)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().source().inputStream()));
                StringBuilder buffer = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    buffer.append(line);
                }
                Gson gson = new Gson();
                gson.toJson(buffer.toString());
                JsonObject jsonObject = gson.fromJson(buffer.toString(), JsonObject.class);
                jsonObject.get("access_token");
                TOKEN = jsonObject.get("access_token").getAsString();
                log.debug("Токен успешно получен");
            } else {
                log.error("Ошибка при получении токена: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            log.error("Ошибка подключения к " + tokenUri, e);
        }
    }

    private static Request getRequest(String jsonString) {
        RequestBody body = RequestBody.create(JSON, jsonString);
        HttpUrl urlBuilder = HttpUrl.get(AppSettings.get("apiUri").toString());
        return new Request.Builder()
                .url(urlBuilder)
                .post(body)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build();
    }


    public static void saveMdmObjects(String jsonString) {
        log.debug("Передадим объекты МДМ по адресу " + AppSettings.get("apiUri").toString());
        OkHttpClient client = HttpClientApi.trustAllSslClient(new OkHttpClient());
        Request request = getRequest(jsonString);
        try (Response response = client.newCall(request).execute()) {
            switch (response.code()) {
                case 401:
                    log.debug("Авторизация не удалась. Повторный запрос токена");
                    getToken();
                    try (Response response2 = client.newCall(request).execute()) {
                        if (response2.code() != 200) {
                            log.error("Произошла ошибка при передачи объекта, сервис вернул: " + response2.message());
                        }
                    }
                    break;
                case 200:
                    log.debug("Объект успешно передан");
                    break;
                default:
                    log.error("Произошла ошибка при передаче объекта, служба вернула: " + response.message());
                    break;
            }
        } catch (IOException e) {
            log.error("Ошибка подключения к " + AppSettings.get("apiUri").toString(), e);
        }
    }
}
