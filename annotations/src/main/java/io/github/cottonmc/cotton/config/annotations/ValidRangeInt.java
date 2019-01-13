package io.github.cottonmc.cotton.config.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.CLASS)
@Inherited
public @interface ValidRangeInt {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
}
