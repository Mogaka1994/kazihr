package com.projectx.pay.globalFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

//@Component
public class GlobalFunctions {

    @Autowired
    Environment env;

    @Value("${pass.secret}")
    private String secret;
    @Value("${mpesa.consumerkey}")
    String customerKey; //= env.getRequiredProperty("mpesa.consumerkey") ;
    @Value("${mpesa.consumersecret}")
    String customerSecret;//= env.getRequiredProperty("mpesa.secret");


    private static final Logger LOG = LoggerFactory.getLogger(GlobalFunctions.class);


    public String hmacDigest(String msg) {
        String digest = null;
        try {//sha256
            SecretKeySpec key = new SecretKeySpec(env.getRequiredProperty("secret").getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.US_ASCII));

            StringBuilder hash = new StringBuilder();
            for (byte aByte : bytes) {
                String hex = Integer.toHexString(0xFF & aByte);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return digest;
    }

    public static String generatePass() {
        Random rand = new Random();
        String aToZ = "ZzMmNnOoPpQqRrSsTtUuVvWwXxYy_AaBbCcDdEeFfGgHhIiJjKkLl1234567890";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            int randIndex = rand.nextInt(aToZ.length());
            res.append(aToZ.charAt(randIndex));
        }
        System.out.println(res.toString());
        return res.toString();
    }

    public static String get(String url, String param, String paymentGatewayAuthentication) {

        String response_string = "";
        try {
            String charset = "UTF-8";
            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(false); // Triggers GET.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
            // connection.setRequestProperty("Authorization", "Basic dGlnZXI6UEdXaTI0Zw==");
            connection.setRequestProperty("Authorization", "Basic " + paymentGatewayAuthentication);
            connection.setRequestProperty("Cache-Control", "no-cache");
            InputStream response = connection.getInputStream();
            response_string = convertStreamToString(response);
            LOG.debug("response : " + response_string);
        } catch (MalformedURLException ex) {
            LOG.debug("Error retrieving exchange rate : " + ex.getMessage());
        } catch (IOException ex) {
            LOG.debug("Error retrieving exchange rate : " + ex.getMessage());
        }
        return response_string;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        return sb.toString();
    }

    public static String post(String url, String param, String paymentGatewayAuthentication) {
        String response_string = "";
        LOG.debug("posting to url >>> " + url);
        try {
            String charset = "UTF-8";
            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
            connection.setRequestProperty("Authorization", "Bearer " + paymentGatewayAuthentication);
            connection.setRequestProperty("Cache-Control", "no-cache");
            try (OutputStream output = connection.getOutputStream()) {
                output.write(param.getBytes(charset));
            } catch (IOException ex) {
            }
            InputStream response = connection.getInputStream();
            response_string = "" + convertStreamToString(response);
            LOG.debug("response : " + response_string);

        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }

        return response_string;
    }

    public String postPayload(String endPointURL, String requestBody, String authKey) throws IOException {
        System.out.println("endPointURL = " + endPointURL + ", requestBody = " + requestBody + ", authKey = " + authKey);
        URL url = new URL(endPointURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/json;charset=UTF-8");
        connection.setRequestProperty("Accept",
                "application/json;charset=UTF-8");
        connection.setRequestProperty("Authorization",
                authKey);
        connection.setRequestProperty("Content-Length", ""
                + requestBody.getBytes().length);
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(
                connection.getOutputStream())) {
            wr.writeBytes(requestBody);
            wr.flush();
        }
        StringBuilder response = new StringBuilder();
        try (InputStream is = connection.getInputStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    public String getRequestPassword(String shortcode, String passkey, String timestamp) {
        return Base64.getEncoder().encodeToString(new StringBuilder().append(shortcode).append(passkey).append(timestamp).toString().getBytes());
    }


}
