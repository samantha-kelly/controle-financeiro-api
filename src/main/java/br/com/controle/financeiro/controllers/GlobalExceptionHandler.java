package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.dto.ErrorResponseDTO;
import br.com.controle.financeiro.services.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(NegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRuntimeException(NegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<List<ErrorResponseDTO>> handle(MethodArgumentNotValidException ex) {
        return extrairMensagens(ex.getBindingResult().getFieldErrors());
    }

    private ResponseEntity<List<ErrorResponseDTO>> extrairMensagens(List<FieldError> fieldErrors) {
        List<ErrorResponseDTO> dto = new ArrayList<>();

        fieldErrors.forEach(e -> {
            String mensagemValidacao = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            ErrorResponseDTO erro = ErrorResponseDTO.builder()
                    .campo(e.getField())
                    .validacao(mensagemValidacao)
                    .build();
            dto.add(erro);
        });

        return ResponseEntity.badRequest().body(dto);
    }
}
