package com.dashaun.service.twitch.streamstatus;

record TokenResponse(
        String access_token,
        int expires_in,
        String token_type
) {}