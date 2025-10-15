package cn.com.wysha.e_mail_master.valid;

import cn.com.wysha.e_mail_master.valid.validator.DomainValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DomainValidator.class)
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainValid {
    String message() default "invalid domain";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

