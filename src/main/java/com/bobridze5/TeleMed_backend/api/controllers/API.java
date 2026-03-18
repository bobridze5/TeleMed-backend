package com.bobridze5.TeleMed_backend.api.controllers;

public final class API {
    public static final String V1 = "/api/v1";

    public static final String USERS = V1 + "/users";
    public static final String AUTH = V1 + "/users/auth";
    public static final String CITY = V1 + "/city";
    public static final String DISH = V1 + "/dishes";

    public static final String PATIENTS = V1 + "/patients";
    public static final String PATIENT_ID = PATIENTS + "/{patientId}";
    public static final String PATIENT_OWNER = PATIENTS + "/me";

    // Patient
    public static final String PATIENT_OWNER_PARAMS_WEIGHT = PATIENT_OWNER + "/weights";
    public static final String PATIENT_OWNER_PARAMS_SYMPTOM = PATIENT_OWNER + "/symptoms";
    public static final String PATIENT_OWNER_PARAMS_BLOOD_PRESSURE = PATIENT_OWNER + "/blood-pressures";
    public static final String PATIENT_OWNER_PARAMS_PHYSICAL_ACTIVITY = PATIENT_OWNER + "/physical-activities";
    public static final String PATIENT_OWNER_PARAMS_GLYCEMIA = PATIENT_OWNER + "/glycemia-records";


    // Doctor
    public static final String PATIENT_PARAMS_WEIGHT = PATIENT_ID + "/weight";
    public static final String PATIENT_PARAMS_BLOOD_PRESSURE = PATIENT_ID + "/blood-pressure";
    public static final String PATIENT_PARAMS_SYMPTOM = PATIENT_ID + "/symptom";
    public static final String PATIENT_PARAMS_PHYSICAL_ACTIVITY = PATIENT_ID + "/physical-activity";
    public static final String PATIENT_PARAMS_GLYCEMIA_RECORD = PATIENT_ID + "/glycemia-record";

    private API() {
    }
}
