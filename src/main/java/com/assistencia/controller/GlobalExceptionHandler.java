package com.assistencia.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;

/** Evita tela de erro tecnica em operacoes de cadastro e consulta. */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String tratarRegraDeNegocio(RuntimeException erro, HttpServletRequest request) {
        return redirecionarComErro(request, erro.getMessage() != null
                ? erro.getMessage() : "Nao foi possivel concluir a operacao.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String tratarIntegridade(DataIntegrityViolationException erro, HttpServletRequest request) {
        return redirecionarComErro(request,
                "Nao foi possivel concluir: verifique os dados e os registros vinculados.");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String tratarValidacao(ConstraintViolationException erro, HttpServletRequest request) {
        return redirecionarComErro(request, "Verifique os campos informados e tente novamente.");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
                       MissingServletRequestParameterException.class})
    public String tratarParametros(Exception erro, HttpServletRequest request) {
        return redirecionarComErro(request, "Parametro invalido ou obrigatorio nao informado.");
    }

    private String redirecionarComErro(HttpServletRequest request, String mensagem) {
        RequestContextUtils.getOutputFlashMap(request).put("erro", mensagem);
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        String destino;
        if (caminho.startsWith("/clientes")) destino = "/clientes";
        else if (caminho.startsWith("/equipamentos")) destino = "/equipamentos";
        else if (caminho.startsWith("/servicos")) destino = "/servicos";
        else if (caminho.startsWith("/usuarios")) destino = "/usuarios";
        else if (caminho.startsWith("/ordens")) destino = "/ordens";
        else destino = "/";

        // Se a propria listagem causou o erro, redireciona para o inicio (evita loop).
        if (caminho.equals(destino)) destino = "/";
        return "redirect:" + destino;
    }
}
