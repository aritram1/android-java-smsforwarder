package com.example.smsforwarderservice.v2.helper;
import android.os.Build;

public class GlobalConstants {
    public static final String APP_NAME = "SMSForwarder";
    public static final String DEVICE_NAME = Build.MANUFACTURER + " " + Build.MODEL;

    public static final String AUTH_ENDPOINT = "https://login.salesforce.com/services/oauth2/authorize";
    public static final String TOKEN_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final String OTP_RECEP_LIST = "Me,Maa";
    public static final String SHORTCODE_HOTSTAR = "HOTSTR";
    public static final String SHORTCODE_HOICHOI = "HOCHOI";
    public static final String SHORTCODE_KLIKK = "KLIKK";
    public static final String POST = "POST";

    public static final String PE_URL = "/services/data/v53.0/sobjects/FinPlan__SMS_Message__c";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String INSTANCE_URL = "instance_url";
    public static final String ID = "id";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ISSUED_AT = "issued_at";
    public static final String SIGNATURE = "signature";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String REDIRECT_URI = "https://localhost";

    public static final String USERNAME2 = "aritram1@gmail.com.financeplanner";
    public static final String PASSWORD2 = "financeplanner123W8oC4taee0H2GzxVbAqfVB14";
    public static final String CLIENT_ID2 = "3MVG9wt4IL4O5wvIBCa0yrhLb82rC8GGk03G2F26xbcntt9nq1JXS75mWYnnuS2rxwlghyQczUFgX4whptQeT";
    public static final String CLIENT_SECRET2 = "3E0A6C0002E99716BD15C7C35F005FFFB716B8AA2DE28FBD49220EC238B2FFC7";
    public static final String RESOURCE_URL2 = "/services/data/v53.0/sobjects/FinPlan__SMS_Message__c";

    // public static final String INSTANCE_URL2 = "https://expenso-dev-ed.develop.my.salesforce.com";
    public static final String USERNAME = "aritram1@gmail.com.expenso";
    public static final String PASSWORD = "expenso11LGIA6Y4NrPyczh3LZTZKgWW";
    public static final String CLIENT_ID = "3MVG9k02hQhyUgQAH_sdqt1SK.dprVVZ7C5oouxnQhMB6PQycuII2sZiJPZD64xJavuyeg3m8.aiX65IFxtAD";
    public static final String CLIENT_SECRET = "81E33EC5923261685884EB5A7DFC0457D8A89ABF80CAE5DC7D1137069151FB7Da";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS2 = "client_credentials";
    public static final String RESOURCE_URL = "/services/data/v59.0/sobjects/SMS_Platform_Event__e";

}
