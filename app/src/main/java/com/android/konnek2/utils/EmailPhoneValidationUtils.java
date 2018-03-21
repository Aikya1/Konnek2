package com.android.konnek2.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajeev on 14/12/17.
 */

public class EmailPhoneValidationUtils {

    private final static String mobileExpression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{9,15}$";

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static boolean isValidPhone(String phone) {

        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(mobileExpression);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.matches();
    }


    public static boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
