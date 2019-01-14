package config;

public class RegexValidator implements StringValidator {
    private String regex;

    public RegexValidator(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    @Override
    public boolean validate(String value) {
        return regex.matches(regex);
    }
}
