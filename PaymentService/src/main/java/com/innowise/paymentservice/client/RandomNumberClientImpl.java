package com.innowise.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RandomNumberClientImpl implements RandomNumberClient {

    private final RestTemplate restTemplate;

    @Value("${external.random-api.url}")
    private String randomApiUrl;

    @Override
    public int getRandomNumber() {
        Integer[] response = restTemplate.getForObject(
                randomApiUrl,
                Integer[].class
        );

        if (response == null || response.length == 0) {
            throw new IllegalStateException("Random API returned empty response");
        }

        return response[0];
    }
}
