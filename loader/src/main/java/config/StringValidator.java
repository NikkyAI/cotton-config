package config;

public interface StringValidator extends ConfigValidator {
    boolean validate(String value);
}
