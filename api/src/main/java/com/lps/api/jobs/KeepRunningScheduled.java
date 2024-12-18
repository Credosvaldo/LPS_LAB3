package com.lps.api.jobs;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeepRunningScheduled {
    @Scheduled(fixedRate = (1000 * 60 * 14))
    public void executeTask() throws ClientProtocolException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://ajuda-ai-backend-npg2.onrender.com/keeprenderon");
        httpClient.execute(request);
    }
}
