package com.bobridze5.TeleMed_backend.api.controllers;

public final class API {
    public static final String V1 = "/api/v1";

    public static final String AUTH = V1 + "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_REGISTER = AUTH + "/register";


    public static final String USERS = V1 + "/users";
    public static final String CITY = V1 + "/city";
    public static final String DISH = V1 + "/dishes";
    public static final String CHATS = V1 + "/chats";
    public static final String ORGANIZATIONS = V1 + "/organizations";

    // Admin
    public static final String ADMIN = V1 + "/admin";
    public static final String ADMIN_DOCTORS = ADMIN + "/doctors";
    public static final String ADMIN_DOCTORS_PENDING = ADMIN_DOCTORS + "/pending";
    public static final String ADMIN_PATIENTS = ADMIN + "/patients";
    public static final String ADMIN_STATS = ADMIN + "/stats";
    public static final String ADMIN_USERS = ADMIN + "/users";
    public static final String ADMIN_ORGANIZATIONS = ADMIN + "/organizations";

    public static final String PATIENTS = V1 + "/patients";
    public static final String PATIENT_ID = PATIENTS + "/{patientId}";
    public static final String PATIENT_ME = PATIENTS + "/me";

    // Patient
    public static final String PATIENT_ME_PARAMS_WEIGHT = PATIENT_ME + "/weights";
    public static final String PATIENT_ME_PARAMS_SYMPTOM = PATIENT_ME + "/symptoms";
    public static final String PATIENT_ME_PARAMS_BLOOD_PRESSURE = PATIENT_ME + "/blood-pressures";
    public static final String PATIENT_ME_PARAMS_PHYSICAL_ACTIVITY = PATIENT_ME + "/physical-activities";
    public static final String PATIENT_ME_PARAMS_GLYCEMIA = PATIENT_ME + "/glycemia";

    public static final String PATIENT_ME_DIARY = PATIENT_ME + "/diary";
    public static final String PATIENT_ME_MEALS = PATIENT_ME + "/meals";
    public static final String PATIENT_ME_MEAL_ID = PATIENT_ME_MEALS + "/{mealId}";
    public static final String PATIENT_ME_MEAL_ITEMS = PATIENT_ME_MEAL_ID + "/items";
    public static final String PATIENT_ME_MEAL_ITEM_ID = PATIENT_ME_MEAL_ITEMS + "/{itemId}";

    public static final String PATIENT_ME_PROFILE = PATIENT_ME + "/profile";
    public static final String PATIENT_ME_TEST = PATIENT_ME + "/test";
    public static final String PATIENT_ME_MEDICAL_CARD = PATIENT_ME + "/card";
    public static final String PATIENT_ME_MEDICAL_RECORDS = PATIENT_ME + "/medical-records";
    public static final String PATIENT_ME_ALLERGIES = PATIENT_ME + "/allergies";
    public static final String PATIENT_ME_MEDICATIONS = PATIENT_ME + "/medications";
    public static final String PATIENT_ME_MEDICAL_EVENTS = PATIENT_ME + "/medical-events";
    public static final String PATIENT_ME_APPOINTMENTS = PATIENT_ME + "/appointments";
    public static final String PATIENT_ME_REPORT = PATIENT_ME + "/report";


    // Doctor
    public static final String DOCTORS = V1 + "/doctors";
    public static final String DOCTOR_ME = DOCTORS + "/me";
    public static final String DOCTOR_ME_PROFILE = DOCTOR_ME + "/profile";
    public static final String DOCTOR_ME_PATIENTS = DOCTOR_ME + "/patients";
    public static final String DOCTOR_ME_PATIENT_ID = DOCTOR_ME_PATIENTS + "/{patientId}";
    public static final String DOCTOR_ME_PATIENT_DIARY = DOCTOR_ME_PATIENT_ID + "/diary";
    public static final String DOCTOR_ME_SCHEDULE = DOCTOR_ME + "/schedule";
    public static final String DOCTOR_ME_APPOINTMENTS = DOCTOR_ME + "/appointments";
    public static final String PATIENT_PARAMS_WEIGHT = PATIENT_ID + "/weights";
    public static final String PATIENT_PARAMS_BLOOD_PRESSURE = PATIENT_ID + "/blood-pressures";
    public static final String PATIENT_PARAMS_SYMPTOM = PATIENT_ID + "/symptoms";
    public static final String PATIENT_PARAMS_PHYSICAL_ACTIVITY = PATIENT_ID + "/physical-activities";
    public static final String PATIENT_PARAMS_GLYCEMIA = PATIENT_ID + "/glycemia";
    public static final String PATIENT_MEALS = PATIENT_ID + "/meals";
    public static final String PATIENT_PROFILE = PATIENT_ID + "/profile";
    public static final String PATIENT_ALLERGIES = PATIENT_ID + "/allergies";
    public static final String PATIENT_MEDICATIONS = PATIENT_ID + "/medications";
    public static final String PATIENT_MEDICAL_EVENTS = PATIENT_ID + "/medical-events";
    public static final String PATIENT_CARD = PATIENT_ID + "/card";
    public static final String PATIENT_MEDICAL_RECORDS = PATIENT_ID + "/medical-records";

    private API() {
    }
}
