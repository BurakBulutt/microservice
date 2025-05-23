package com.example.servicereaction.domain.comment.validator;

import com.example.servicereaction.domain.comment.api.AddCommentRequest;
import com.example.servicereaction.domain.comment.enums.CommentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCommentValidator implements ConstraintValidator<AddCommentValidation, AddCommentRequest> {
    @Override
    public boolean isValid(AddCommentRequest request, ConstraintValidatorContext context) {
        if (request == null) return true;

        if (request.type().equals(CommentType.REPLY) && request.parentId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("validation.comment.parentId.notNull")
                    .addPropertyNode("parentId")
                    .addConstraintViolation();
            return false;
        }

        if (request.type().equals(CommentType.COMMENT) && request.targetId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("validation.comment.targetId.notNull")
                    .addPropertyNode("targetId")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
