package com.assistencia.controller;

import com.assistencia.service.OrdemServicoService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ComprovanteController {

    private final OrdemServicoService ordemServicoService;

    public ComprovanteController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping("/ordens/{id}/imprimir")
    public String imprimir(@PathVariable("id") Long id, Model model) {
        model.addAttribute("os", ordemServicoService.buscarPorId(id));
        model.addAttribute("dataEmissao", LocalDateTime.now());
        return "ordens/impressao";
    }
}
