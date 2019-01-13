package io.github.cottonmc.cotton.config.annotations;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.CLASS)
@Inherited
public @interface ValidValuesString {
    String[] values();
}
