package com.wahwahnetworks.platform.annotations;

import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Justin on 5/17/2014.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HasUserRole
{
	UserRoleType[] value();
}
