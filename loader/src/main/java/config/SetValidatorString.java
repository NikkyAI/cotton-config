package config;

import java.util.Set;

public class SetValidatorString implements StringValidator {
    private Set<String> validValues;

    public SetValidatorString(Set<String> validValues) {
        this.validValues = validValues;
    }

    public Set<String> getValidValues() {
        return validValues;
    }

    @Override
    public boolean validate(String value) {
        return validValues.contains(value);
    }
}
