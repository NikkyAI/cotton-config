package io.github.cottonmc.cotton.config.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
public @interface RegexValidator {
    String regex();
    int[] typeIndex() default {0};
}
