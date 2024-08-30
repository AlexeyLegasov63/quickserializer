package org.karma.serialization;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface SerializerObject {
	short signature();
	int bytes() default 1024;
}
