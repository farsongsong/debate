package com.example.portfolio.global.exception;

import com.example.portfolio.global.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * REST 요청(Accept: application/json 또는 /api/** )은 JSON 응답,
     * 일반 Thymeleaf 요청은 error 페이지로 redirect.
     */
    @ExceptionHandler(CustomException.class)
    public Object handleCustomException(CustomException e, HttpServletRequest request) {
        log.warn("CustomException: {} - {}", e.getErrorCode(), e.getMessage());

        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(e.getErrorCode().getStatus())
                    .body(ResponseDto.fail(e.getMessage()));
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(e.getErrorCode().getStatus());
        mav.addObject("errorCode", e.getErrorCode().getStatus().value());
        mav.addObject("errorMessage", e.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception: {}", e.getMessage(), e);

        if (isApiRequest(request)) {
            return ResponseEntity
                    .status(500)
                    .body(ResponseDto.fail("서버 오류가 발생했습니다."));
        }

        ModelAndView mav = new ModelAndView("error/error");
        mav.setStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("errorCode", 500);
        mav.addObject("errorMessage", "서버 오류가 발생했습니다.");
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/") ||
               (accept != null && accept.contains("application/json") && !accept.contains("text/html"));
    }
}
