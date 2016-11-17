package ru.sbrf.schoolchat.validators;

/**
 * Created by Ivan on 17/11/2016.
 */
public class LoginValidator implements Validator {
    @Override
    public boolean isValid(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }
}
