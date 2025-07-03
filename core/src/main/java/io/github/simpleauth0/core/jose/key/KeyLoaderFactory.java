package io.github.simpleauth0.core.jose.key;

import io.github.simpleauth0.core.SimpleAuth0Properties;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:33
 */
public class KeyLoaderFactory {

    private final SimpleAuth0Properties properties;
    private final List<KeyLoader> keyLoaders;

    public KeyLoaderFactory(SimpleAuth0Properties properties, List<KeyLoader> keyLoaders) {
        this.properties = properties;
        this.keyLoaders = keyLoaders;
    }

    public KeyLoader getKeyLoader() {
        KeySource source = properties.getToken().getJwt().getKeySource();
        return keyLoaders.stream()
                .filter(loader -> loader.supports(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported key source: " + source));
    }
}


