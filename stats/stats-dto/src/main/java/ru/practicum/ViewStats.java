package ru.practicum;

import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ViewStats {
    String app;
    String uri;
    long hits;

    public ViewStats() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    public ViewStats(String uri, String app, Long hits) {
        this.uri = uri;
        this.app = app;
        this.hits = hits;
    }
}
