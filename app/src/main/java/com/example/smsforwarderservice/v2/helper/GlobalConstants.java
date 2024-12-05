package com.example.smsforwarderservice.v2.helper;
import android.os.Build;

import java.util.List;

public class GlobalConstants {
    public static final String APP_NAME = "SMSForwarder";
    public static final String DEVICE_NAME = Build.MANUFACTURER + " " + Build.MODEL;
    public static final String AUTH_ENDPOINT = "https://login.salesforce.com/services/oauth2/authorize";
    public static final String TOKEN_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
    public static final String TOKEN_ENDPOINT_FOR_CLIENT_CREDENTIALS_FLOW = "https://expenso-dev-ed.develop.my.salesforce.com/services/oauth2/token";
    public static final String USERNAME = "aritram1@gmail.com.expenso";
    public static final String PASSWORD = "expenso1hmjmTb7kYcROHD6FtOYGPVVO";
    public static final String CLIENT_ID = "3MVG9k02hQhyUgQAH_sdqt1SK.UP_KJVv9NytGFYs80P8fNz8sdqnag.zaC3dDd_98BlhPqj07oielhVpx51U";
    public static final String CLIENT_SECRET = "9217258B3C870B6DEF1CD180B59F5161400691A0896CCE60BF79C2FB34650677";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String RESOURCE_URL = "/services/data/v59.0/sobjects/SMS_Platform_Event__e";

    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String OTP_RECEP_LIST = "Me,Maa";
    public static final String SHORTCODE_HOTSTAR = "HOTSTR";
    public static final String SHORTCODE_HOICHOI = "HOCHOI";
    public static final String SHORTCODE_KLIKK = "KLIKK";
    public static final String POST = "POST";
    public static final String REDIRECT_URI = "http://localhost";

    /*public static final String USERNAME2 = "aritram1@gmail.com.financeplanner";
    public static final String PASSWORD2 = "financeplanner123W8oC4taee0H2GzxVbAqfVB14";
    public static final String CLIENT_ID2 = "3MVG9wt4IL4O5wvIBCa0yrhLb82rC8GGk03G2F26xbcntt9nq1JXS75mWYnnuS2rxwlghyQczUFgX4whptQeT";
    public static final String CLIENT_SECRET2 = "3E0A6C0002E99716BD15C7C35F005FFFB716B8AA2DE28FBD49220EC238B2FFC7";
    public static final String RESOURCE_URL2 = "/services/data/v53.0/sobjects/FinPlan__SMS_Message__c";
    public static final String PE_URL = "/services/data/v53.0/sobjects/FinPlan__SMS_Message__c";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String INSTANCE_URL = "instance_url";
    public static final String ID = "id";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ISSUED_AT = "issued_at";
    public static final String SIGNATURE = "signature";
    public static final String GRANT_TYPE_PASSWORD = "password";*/

    public static final List<String> TRANSACTIONAL_KEYWORDS = List.of(
            "rs ","sent rs.",
            "amount","amt",
            "credited","debited","money received", "money sent",
            "bank account","bank card",
            "balance","available bal",
            "a/c xx9560", "a/c *9560"
    );
}
