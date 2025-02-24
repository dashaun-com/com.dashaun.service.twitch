package com.dashaun.service.twitch.streamstatus;

record StreamStatus(
        boolean isLive,
        String streamTitle,    // optional: additional info about the stream
        int viewerCount       // optional: current viewers if live
) {}
