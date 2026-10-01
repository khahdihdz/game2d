package com.darkfantasy.game;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;

public class WebLauncher {
    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.useGL30 = true;
        config.showDownloadLogs = false;
        new WebApplication(new DarkFantasyGame(), config);
    }
}
