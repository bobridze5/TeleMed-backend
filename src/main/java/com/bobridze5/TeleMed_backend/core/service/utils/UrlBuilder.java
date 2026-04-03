package com.bobridze5.TeleMed_backend.core.service.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.Map;

public interface UrlBuilder {
    URI buildUrl(HttpServletRequest request, String path);

    URI buildUrl(HttpServletRequest request, String path, Map<String, String> values);

    URI buildUrl(HttpServletRequest request, String path,
                 Map<String, String> pathVariables,
                 Map<String, String> queryParams);

    URI buildAbsoluteUrl(HttpServletRequest request, String path);

    URI buildRelativeUrl(String path);
}
