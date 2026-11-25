package com.assistencia.controller;

import com.assistencia.dto.ServicoDTO;
import com.assistencia.model.Servico;
import com.assistencia.service.ServicoService;
import jakarta.validation.Valid;
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
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicos", servicoService.listarTodos());
        return "servicos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new ServicoDTO());
        return "servicos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        Servico servico = servicoService.buscarPorId(id);
        ServicoDTO dto = new ServicoDTO();
        dto.setId(servico.getId());
        dto.setNome(servico.getNome());
        dto.setDescricao(servico.getDescricao());
        dto.setPrecoBase(servico.getPrecoBase());

        model.addAttribute("servico", dto);
        return "servicos/formulario";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("servico") ServicoDTO dto,
                         BindingResult erros,
                         RedirectAttributes flash) {
        if (erros.hasErrors()) {
            return "servicos/formulario";
        }

        Servico servico = new Servico();
        servico.setId(dto.getId());
        servico.setNome(dto.getNome().trim());
        servico.setDescricao(dto.getDescricao() != null ? dto.getDescricao().trim() : null);
        servico.setPrecoBase(dto.getPrecoBase());

        if (dto.getId() == null) {
            servicoService.salvar(servico);
            flash.addFlashAttribute("sucesso", "Serviço cadastrado com sucesso!");
        } else {
            servicoService.atualizar(dto.getId(), servico);
            flash.addFlashAttribute("sucesso", "Serviço atualizado com sucesso!");
        }

        return "redirect:/servicos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            servicoService.excluir(id);
            flash.addFlashAttribute("sucesso", "Serviço excluído com sucesso!");
        } catch (Exception e) {
            flash.addFlashAttribute("erro", "Não foi possível excluir o serviço: " + e.getMessage());
        }
        return "redirect:/servicos";
    }
}
