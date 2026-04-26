package com.bobridze5.TeleMed_backend.core.service.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
public class UrlBuilder {
    public URI buildUrl(HttpServletRequest request, String path) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(path)
                .build()
                .toUri();
    }

    public URI buildUrl(HttpServletRequest request, String path, Map<String, String> values) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(path)
                .buildAndExpand(values)
                .toUri();
    }

    public URI buildUrl(HttpServletRequest request, String path,
                        Map<String, String> pathVariables,
                        Map<String, String> queryParams
    ) {
        var builder = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(path);
        queryParams.forEach(builder::queryParam);
        return builder.buildAndExpand(pathVariables).toUri();
    }

    public URI buildAbsoluteUrl(HttpServletRequest request, String path) {
        return ServletUriComponentsBuilder.fromContextPath(request)
                .path(path)
                .build()
                .toUri();
    }

    public URI buildRelativeUrl(String path) {
        return URI.create(path);
    }
}
