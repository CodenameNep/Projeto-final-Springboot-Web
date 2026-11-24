package com.assistencia.controller;

import com.assistencia.dto.AberturaOrdemServicoDTO;
import com.assistencia.model.OrdemServico;
import com.assistencia.service.ClienteService;
import com.assistencia.service.EquipamentoService;
import com.assistencia.service.OrdemServicoService;
import com.assistencia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final ClienteService clienteService;
    private final EquipamentoService equipamentoService;
    private final UsuarioService usuarioService;

    public OrdemServicoController(OrdemServicoService ordemServicoService,
                                  ClienteService clienteService,
                                  EquipamentoService equipamentoService,
                                  UsuarioService usuarioService) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.equipamentoService = equipamentoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordens", ordemServicoService.listarTodas());
        return "ordens/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("ordem", new AberturaOrdemServicoDTO());
        carregarOpcoesAbertura(model);
        return "ordens/abertura";
    }

    @PostMapping
    public String abrir(@Valid @ModelAttribute("ordem") AberturaOrdemServicoDTO dto,
                        BindingResult erros, Authentication authentication,
                        Model model, RedirectAttributes flash) {
        if (erros.hasErrors()) {
            carregarOpcoesAbertura(model);
            return "ordens/abertura";
        }
        try {
            OrdemServico ordem = ordemServicoService.abrirOrdem(
                    clienteService.buscarPorId(dto.getClienteId()),
                    equipamentoService.buscarPorId(dto.getEquipamentoId()),
                    usuarioService.buscarPorEmail(authentication.getName()),
                    dto.getDefeitoRelatado());
            flash.addFlashAttribute("sucesso", "Ordem de serviço aberta com sucesso.");
            return "redirect:/ordens/" + ordem.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            carregarOpcoesAbertura(model);
            return "ordens/abertura";
        }
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("os", ordemServicoService.buscarPorId(id));
            return "ordens/detalhes";
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/ordens";
        }
    }

    private void carregarOpcoesAbertura(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("equipamentos", equipamentoService.listarTodos());
    }
}
