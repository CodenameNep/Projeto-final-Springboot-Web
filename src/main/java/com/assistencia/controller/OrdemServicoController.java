package com.assistencia.controller;

import com.assistencia.dto.AberturaOrdemServicoDTO;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.service.ClienteService;
import com.assistencia.service.EquipamentoService;
import com.assistencia.service.OrdemServicoService;
import com.assistencia.service.ServicoService;
import com.assistencia.service.UsuarioService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final ClienteService clienteService;
    private final EquipamentoService equipamentoService;
    private final UsuarioService usuarioService;
    private final ServicoService servicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService,
                                  ClienteService clienteService,
                                  EquipamentoService equipamentoService,
                                  UsuarioService usuarioService,
                                  ServicoService servicoService) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.equipamentoService = equipamentoService;
        this.usuarioService = usuarioService;
        this.servicoService = servicoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(name = "status", required = false) StatusOrdemServico status,
            @RequestParam(name = "clienteNome", required = false) String clienteNome,
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        List<OrdemServico> ordens = ordemServicoService.buscarComFiltros(status, clienteNome, dataInicio, dataFim);
        boolean filtrosAtivos = status != null
                || (clienteNome != null && !clienteNome.isBlank())
                || dataInicio != null
                || dataFim != null;

        model.addAttribute("ordens", ordens);
        model.addAttribute("statusList", StatusOrdemServico.values());
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("clienteNomeFiltro", clienteNome);
        model.addAttribute("dataInicioFiltro", dataInicio);
        model.addAttribute("dataFimFiltro", dataFim);
        model.addAttribute("filtrosAtivos", filtrosAtivos);
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
    public String detalhes(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("os", ordemServicoService.buscarPorId(id));
            model.addAttribute("servicos", servicoService.listarTodos());
            return "ordens/detalhes";
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/ordens";
        }
    }

    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            ordemServicoService.atualizarStatus(id,
                    StatusOrdemServico.EM_ANDAMENTO);
            flash.addFlashAttribute("sucesso", "OS iniciada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable("id") Long id, @RequestParam("laudoTecnico") String laudoTecnico,
                            RedirectAttributes flash) {
        try {
            ordemServicoService.finalizarOrdem(id, laudoTecnico);
            flash.addFlashAttribute("sucesso", "OS finalizada com laudo e data de conclusão.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(@PathVariable("id") Long id, @RequestParam("servicoId") Long servicoId,
                                @RequestParam("quantidade") Integer quantidade, RedirectAttributes flash) {
        try {
            ordemServicoService.adicionarServico(id, servicoService.buscarPorId(servicoId), quantidade);
            flash.addFlashAttribute("sucesso", "Serviço adicionado e total recalculado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/itens/{itemId}/quantidade")
    public String alterarQuantidade(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId,
                                    @RequestParam("quantidade") Integer quantidade, RedirectAttributes flash) {
        try {
            ordemServicoService.alterarQuantidade(id, itemId, quantidade);
            flash.addFlashAttribute("sucesso", "Quantidade e total atualizados.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/itens/{itemId}/remover")
    public String removerItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId,
                              RedirectAttributes flash) {
        try {
            ordemServicoService.removerServico(id, itemId);
            flash.addFlashAttribute("sucesso", "Serviço removido e total recalculado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    private void carregarOpcoesAbertura(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("equipamentos", equipamentoService.listarTodos());
    }
}
