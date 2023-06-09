package com.projectx.pay.globalFunctions;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@Transactional
public class BeareTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(BeareTokenService.class);
    public static String token = "";
    private static final Set<String> _safaricomPrefixes = new LinkedHashSet<>();

    @Autowired
    Environment env;

    @PostConstruct
    public void init() {
        try {
            for (int i = 254700; i < 254730; i++) {
                _safaricomPrefixes.add(String.valueOf(i));
            }
            _safaricomPrefixes.add("254790");
            _safaricomPrefixes.add("254791");
            _safaricomPrefixes.add("254792");
            autoSetOauthBearerCode();
            LOG.info("BearerTokenService init token={}", token);
        } catch (Exception e) {
            LOG.error("Error when initializing BearerTokenService for token fetch={}", e);
        }
    }

    public static boolean isSafaricomNumber(String phoneNo) {
        return _safaricomPrefixes.contains(phoneNo.substring(0, 6));
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 60 * 30)
    public void autoSetOauthBearerCode() {
        try {
            token = generateAccessToken();
            LOG.info("Refreshed token={}", token);
        } catch (Exception e) {
            LOG.error("Error fetching token ={}", e);
        }
    }

    private String generateAccessToken() {

        String accessToken = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String authUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
            String customerKey = "mAbzG8t0PQXnyBmZNsCqPyUovnTC9W1c";
            String customerSecret = "GO1HVqoeA3FAOGrH";

            String encryptByte = customerKey + ":" + customerSecret;
            String encoded = Base64.getEncoder().encodeToString(encryptByte.getBytes());
            String authorizationString = "Basic " + encoded;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", authorizationString);
            headers.add("Access-Control-Allow-Origin", "*");

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            LOG.info("auth url: " + authUrl + ", request : " + requestEntity.toString());

            ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.GET, requestEntity, String.class);
            JSONObject authResponse = new JSONObject(response.getBody());
            LOG.info("authJsonResponse >>> " + authResponse);

            if (authResponse.toString().toUpperCase().contains("ACCESS")) {
                accessToken = authResponse.getString("access_token");
                LOG.info("access_token : " + accessToken);
                String expires_in = authResponse.getString("expires_in");
                LOG.info("expires_in : " + expires_in);
            }
        } catch (Exception e) {
            LOG.info("Encountered error in bearer authentication : " + e.getMessage());
        }
        return accessToken;
    }

}