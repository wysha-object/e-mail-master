package cn.com.wysha.e_mail_master.valid.validator;

import cn.com.wysha.e_mail_master.valid.DomainValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DomainValidator implements ConstraintValidator<DomainValid, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;
        return s.matches("^(?!^\\.)(?!(?:^|.*\\.)(?:|[A-Za-z0-9-]{64,}|[A-Za-z0-9-]*-|-[A-Za-z0-9-]*)(?:$|\\.))[A-Za-z0-9-.]{1,253}$");
    }
}
