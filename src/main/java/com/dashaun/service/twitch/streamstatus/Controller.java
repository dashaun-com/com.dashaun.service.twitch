package com.dashaun.service.twitch.streamstatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/twitch")
class Controller {
    private final Service service;

    Controller(Service service) {
        this.service = service;
    }

    @GetMapping("/stream-status")
    public Mono<ResponseEntity<StreamStatus>> getStreamStatus() {
        return service.getAccessToken()
                .flatMap(token -> service.checkStreamStatus(token.access_token()))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new StreamStatus(false, "Error checking stream status", 0))
                ));
    }
}
