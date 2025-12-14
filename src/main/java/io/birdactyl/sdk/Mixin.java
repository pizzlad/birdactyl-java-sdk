package io.birdactyl.sdk;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mixin {
    String value();
    int priority() default 0;
}
