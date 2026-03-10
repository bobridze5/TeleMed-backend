package com.bobridze5.TeleMed_backend.api.controllers;

public final class API {
    public static final String V1 = "/api/v1";

    public static final String USERS = V1 + "/users";
    public static final String AUTH = V1 + "/users/auth";
    public static final String CITY = V1 + "/city";
    public static final String DISH = V1 + "/dishes";

    public static final String PARAMS_WEIGHT = V1 + "/weight";

    private API() {
    }
}
