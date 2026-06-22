package com.kt.millet.helm.test_dev;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "abclab.api")
public class AbcLabProperties {
    private String url;
    private String key;
    private String add_url;
    private String add_key;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public String getAddUrl() {
        return add_url;
    }

    public void setAddUrl(String add_url) {
        this.add_url = add_url;
    }

    public String getAddKey() {
        return add_key;
    }

    public void setAddKey(String add_key) {
        this.add_key = add_key;
    }
}
