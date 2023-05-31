package com.enterpriseintellijence.enterpriseintellijence.security;

public class Constants {

    public static final String TOKEN_SECRET_KEY = "KEcrS2JQZVNoVm1ZcTN0Nnc5eiRDJkYpSEBNY1FmVGo=";
    public static final int JWT_EXPIRATION_TIME = 1;  // in hours
    public static final int JWT_REFRESH_EXPIRATION_TIME = 24;  // in hours
    public static final int JWT_CAPABILITY_EXPIRATION_TIME = 1;  // in hours
    public static final int EMAIL_VERIFICATION_TOKEN_EXPIRATION_TIME = 24;  // in hours
    public static final String VERIFICATION_EMAIL_SUBJECT = "Enterprise Intelligence - Email Verification";
    public static final String VERIFICATION_EMAIL_TEXT = "Please click the link below to verify your email address and complete your registration.\n";
    public static final String REFRESH_TOKEN_CLAIM = "refresh-token";
    public static final String EMAIL_VERIFICATION_CLAIM = "email-verification";
    public static final String RESET_PASSWORD_CLAIM = "reset-password";
    public static final String RESET_PASSWORD_EMAIL_SUBJECT = "Enterprise Intelligence - Reset Password";
    public static final String RESET_PASSWORD_EMAIL_TEXT = "Please click the link below to reset your password.\n";
    public static final String NEW_PASSWORD_EMAIL_SUBJECT = "Enterprise Intelligence - New Password";
    public static final String NEW_PASSWORD_EMAIL_TEXT = "Your new password is: ";
}
