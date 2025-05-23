package com.example.servicereaction.domain.comment.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {AddCommentValidator.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AddCommentValidation {
    String message() default "{validation.comment.addComment}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
