package io.github.cottonmc.cotton.config.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
public @interface RangeValidatorFloat {
    float min() default Float.MIN_VALUE;
    float max() default Float.MAX_VALUE;
    int[] typeIndex() default {0};
}
