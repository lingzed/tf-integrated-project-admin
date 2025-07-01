package com.ruoyi.common.validated.json;

import com.ruoyi.common.constant.MsgConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = JsonContValidator.class)
public @interface JsonCont {
    String message() default MsgConstants.JSON_CONT_NOT_LEGAL_FORMAT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
