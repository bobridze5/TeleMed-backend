package com.bobridze5.TeleMed_backend.api.controllers;

public final class API {
    public static final String V1 = "/api/v1";

    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_REGISTER = AUTH + "/register";


    public static final String USERS = V1 + "/users";
    public static final String AUTH_OLD = V1 + "/users/auth"; // TODO: переделать
    public static final String CITY = V1 + "/city";
    public static final String DISH = V1 + "/dishes";

    public static final String PATIENTS = V1 + "/patients";
    public static final String PATIENT_ID = PATIENTS + "/{patientId}";
    public static final String PATIENT_ME = PATIENTS + "/me";

    // Patient
    public static final String PATIENT_ME_PARAMS_WEIGHT = PATIENT_ME + "/weights";
    public static final String PATIENT_ME_PARAMS_SYMPTOM = PATIENT_ME + "/symptoms";
    public static final String PATIENT_ME_PARAMS_BLOOD_PRESSURE = PATIENT_ME + "/blood-pressures";
    public static final String PATIENT_ME_PARAMS_PHYSICAL_ACTIVITY = PATIENT_ME + "/physical-activities";
    public static final String PATIENT_ME_PARAMS_GLYCEMIA = PATIENT_ME + "/glycemia";

    public static final String PATIENT_ME_PROFILE = PATIENT_ME + "/profile";
    public static final String PATIENT_ME_TEST = PATIENT_ME + "/test";
    public static final String PATIENT_ME_MEDICAL_CARD = PATIENT_ME + "/card";
    public static final String PATIENT_ME_APPOINTMENTS = PATIENT_ME + "/appointments";


    // Doctor
    public static final String PATIENT_PARAMS_WEIGHT = PATIENT_ID + "/weight";
    public static final String PATIENT_PARAMS_BLOOD_PRESSURE = PATIENT_ID + "/blood-pressure";
    public static final String PATIENT_PARAMS_SYMPTOM = PATIENT_ID + "/symptom";
    public static final String PATIENT_PARAMS_PHYSICAL_ACTIVITY = PATIENT_ID + "/physical-activity";
    public static final String PATIENT_PARAMS_GLYCEMIA_RECORD = PATIENT_ID + "/glycemia";

    private API() {
    }
}
