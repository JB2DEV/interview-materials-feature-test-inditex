package com.interview.materials.feature.test.inditex.infraestructure.web.error;


import com.interview.materials.feature.test.inditex.domain.exception.AssetNotFoundException;
import com.interview.materials.feature.test.inditex.domain.exception.InvalidAssetException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(AssetNotFoundException.class)
    public ProblemDetail handleAssetNotFound(AssetNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Asset Not Found");
        return detail;
    }

    @ExceptionHandler(InvalidAssetException.class)
    public ProblemDetail handleInvalidAsset(InvalidAssetException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Invalid Asset");
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ex.printStackTrace();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred.");
        detail.setTitle("Internal Server Error");
        return detail;
    }
}