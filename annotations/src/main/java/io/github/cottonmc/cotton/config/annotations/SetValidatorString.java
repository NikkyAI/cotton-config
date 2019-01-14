package io.github.cottonmc.cotton.config.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
public @interface SetValidatorString {
    String[] values();
    int[] typeIndex() default {0};
}
