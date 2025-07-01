package com.ruoyi.common.validated.json;

import com.ruoyi.common.utils.json.JsonUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JsonContValidator implements ConstraintValidator<JsonCont, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return JsonUtil.isValidJson(s);
    }
}
