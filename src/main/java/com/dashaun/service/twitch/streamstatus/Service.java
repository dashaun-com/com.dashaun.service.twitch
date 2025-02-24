package com.dashaun.service.twitch.streamstatus;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@org.springframework.stereotype.Service
class Service {
    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${twitch.client-secret}")
    private String clientSecret;

    @Value("${twitch.channel-name}")  // Add this to your application.properties
    private String channelName;

    private final WebClient webClient;

    public Service(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TokenResponse> getAccessToken() {
        String url = String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=client_credentials",
                clientId, clientSecret);

        return webClient
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    Mono<StreamStatus> checkStreamStatus(String accessToken) {
        return webClient
                .get()
                .uri("https://api.twitch.tv/helix/streams?user_login=" + channelName)
                .header("Authorization", "Bearer " + accessToken)
                .header("Client-Id", clientId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode data = response.get("data");
                    boolean isLive = !data.isEmpty();
                    if (isLive) {
                        JsonNode streamData = data.get(0);
                        return new StreamStatus(
                                true,
                                streamData.get("title").asText(),
                                streamData.get("viewer_count").asInt()
                        );
                    }
                    return new StreamStatus(false, null, 0);
                });
    }
}
