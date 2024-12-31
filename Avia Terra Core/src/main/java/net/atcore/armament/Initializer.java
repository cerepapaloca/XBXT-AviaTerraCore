package net.atcore.armament;

import jdk.jfr.Experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Experimental
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Initializer {
}
