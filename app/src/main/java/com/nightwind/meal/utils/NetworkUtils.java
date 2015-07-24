package com.nightwind.meal.utils;

import android.content.Context;
import android.util.Log;

import com.nightwind.meal.R;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by nightwind on 15/4/11.
 */
public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();

    public static String SERVER_HOST = "https://omo.nw4869.cc:9443/onlineMealOrdering/rest";

    private Context context;

    private static SSLContext sslContext;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public static int Exception2Code(Exception e) {
        int resultCode = 200;
        if (e instanceof NoLoginException) {
            resultCode = 401;
        } else if (e instanceof HttpForbiddenException) {
            resultCode = 403;
        } else if (e instanceof IOException) {
            resultCode = 500;
        }
        return resultCode;
    }

    public void officialHttpsExample() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        // Tell the URLConnection to use a SocketFactory from our SSLContext
        URL url = new URL("https://certs.cac.washington.edu/CAtest/");
        HttpsURLConnection urlConnection =
                (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        InputStream in = urlConnection.getInputStream();
//        copyInputStreamToOutputStream(in, System.out);
    }


    private SSLSocketFactory getSSLSocketFactory() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException {

        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.server);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

//        KeyStore keyStore = KeyStore.login("BKS");
//        trustStore.load(context.getResources().openRawResource(R.raw.server_trust), "***".toCharArray());

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    public HttpsURLConnection getConnection(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpsURLConnection urlConnection =
                (HttpsURLConnection) url.openConnection();
        try {
            urlConnection.setSSLSocketFactory(getSSLSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlConnection;
    }

    private void sendRequest(HttpsURLConnection connection, String data) throws IOException {
        DataOutputStream wr = new DataOutputStream (
                connection.getOutputStream ());
//        wr.writeBytes (data);
        wr.write(data.getBytes("UTF-8"));
        wr.flush();
        wr.close();
    }

    /**
     *
     * @param targetURL
     * @param urlParameters
     *  String urlParameters =
     *  "fName=" + URLEncoder.encode("???", "UTF-8") +
     *  "&lName=" + URLEncoder.encode("???", "UTF-8");
     * @return
     * @throws IOException
     */
    public String execute(String targetURL, String urlParameters, String requestMethod, Map<String, String> headers) throws IOException, HttpBadRequestException, HttpForbiddenException {

        Log.d(TAG, requestMethod + " " + targetURL + " " + urlParameters);

        if (requestMethod.equals("GET") && urlParameters != null && urlParameters.length() > 0) {
            targetURL += "?" + urlParameters;
        }
        HttpsURLConnection connection = getConnection(targetURL);
        try {
            //Create connection
            connection.setUseCaches(false);
            connection.setDoInput(true);
//            connection.setRequestProperty("Content-Length", "" +
//                    Integer.toString(urlParameters.getBytes().length));
//            connection.setRequestProperty("Content-Language", "en-US");

            //set headers
            if (headers != null) {
                for (String key: headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }

            connection.setRequestMethod(requestMethod);
            if (requestMethod.equals("GET")) {

            } else {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //Send request
                sendRequest(connection, urlParameters);
            }

            //Get Response
            int resCode = connection.getResponseCode();
            String msg = connection.getResponseMessage();
            Log.d(TAG, "response code = " + resCode + " msg = " + msg);
            if (resCode == 400) {
                InputStream in = connection.getErrorStream();
                String result = inputStream2String(in);
                Log.d(TAG, "error result = " + result);
                in.close();
                String error = result;
                try {
                    error = new JSONObject(result).getString("error");
                } catch (JSONException ignored) {
                }
                throw new HttpBadRequestException(error);
            } else if (resCode == 403) {
                InputStream in = connection.getErrorStream();
                String result = inputStream2String(in);
                Log.d(TAG, "error result = " + result);
                in.close();
                String error = result;
                try {
                    error = new JSONObject(result).getString("error");
                } catch (JSONException ignored) {
                }
                throw new HttpForbiddenException(error);
            }

            InputStream in = connection.getInputStream();

            String result = inputStream2String(in);
            Log.d(TAG, "result = " + result);
            in.close();
            return result;

        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public String executeTest(String strUrl) throws IOException {

        HttpsURLConnection connection = getConnection(strUrl);

        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "nw1");

        String param = "username=nw1&password=nw1&name=nw1";
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        sendRequest(connection, param);

        String result = inputStream2String(connection.getInputStream());
        System.out.println(result);
        return result;
    }

    public String executeGet(String targetURL, Map<String, String> urlParameters, Map<String, String> headers) throws IOException, HttpForbiddenException, HttpBadRequestException {
        StringBuilder encodeParams = new StringBuilder();
        if (urlParameters != null && urlParameters.size() > 0) {
            for (String key: urlParameters.keySet()) {
                if (encodeParams.length() > 0) {
                    encodeParams.append("&");
                }
                encodeParams.append(key).append("=").append(URLEncoder.encode(urlParameters.get(key), "UTF-8"));
            }
        }
        return execute(targetURL, encodeParams.toString(), "GET", headers);
    }


    public String executePost(String targetURL, String urlParameters, Map<String, String> headers) throws IOException , HttpForbiddenException, HttpBadRequestException {
        return execute(targetURL, urlParameters, "POST", headers);
    }

    public String executePut(String targetURL, String urlParameters, Map<String, String> headers) throws IOException , HttpForbiddenException, HttpBadRequestException {
        return execute(targetURL, urlParameters, "PUT", headers);
    }

    public String executeDelete(String targetURL, String urlParameters, Map<String, String> headers) throws IOException , HttpForbiddenException, HttpBadRequestException {
        return execute(targetURL, urlParameters, "DELETE", headers);
    }

    final static int BUFFER_SIZE = 4096;

    /**
     * 将InputStream转换成String
     * @param in InputStream
     * @return String
     * @throws IOException
     *
     */
    public static String inputStream2String(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        return new String(outStream.toByteArray(), "UTF-8");
    }

}
