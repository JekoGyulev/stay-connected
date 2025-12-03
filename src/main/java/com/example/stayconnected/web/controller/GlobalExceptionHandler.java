package com.example.stayconnected.web.controller;

import com.example.stayconnected.utils.exception.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailAlreadyExists.class, UsernameAlreadyExists.class})
    public String usernameAlreadyExists(Exception e, RedirectAttributes redirectAttributes) {

        if (e instanceof EmailAlreadyExists) {
            redirectAttributes.addFlashAttribute("emailErrorMessage", e.getMessage());
        } else if (e instanceof UsernameAlreadyExists) {
            redirectAttributes.addFlashAttribute("usernameErrorMessage", e.getMessage());
        }

        return "redirect:/auth/register";
    }

    @ExceptionHandler({PropertyDoesNotExist.class, WalletDoesNotExist.class, UserDoesNotExist.class ,NoResourceFoundException.class})
    public ModelAndView handleNoResourceFoundException(Exception e) {
        return new ModelAndView("/error/not-found-error-page");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ModelAndView handleAccessDeniedException(Exception e) {
        return new ModelAndView("/error/unauthorized-error-page");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException(Exception e) {
        return new ModelAndView("/error/bad-request-error-page");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleLeftoverExceptions(Exception e) {
        return new ModelAndView("/error/internal-server-error");
    }










}
