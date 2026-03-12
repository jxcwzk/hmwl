package com.hmwl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "distance")
public class DistanceConfig {

    private String mode = "straight";

    private MapConfig map = new MapConfig();

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public MapConfig getMap() {
        return map;
    }

    public void setMap(MapConfig map) {
        this.map = map;
    }

    public static class MapConfig {
        private String provider = "tencent";
        private String apiKey;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
