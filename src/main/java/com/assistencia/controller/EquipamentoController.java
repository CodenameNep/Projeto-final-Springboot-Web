package com.assistencia.controller;

import com.assistencia.dto.EquipamentoDTO;
import com.assistencia.model.Cliente;
import com.assistencia.model.Equipamento;
import com.assistencia.service.ClienteService;
import com.assistencia.service.EquipamentoService;
import jakarta.validation.Valid;
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
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;
    private final ClienteService clienteService;

    public EquipamentoController(EquipamentoService equipamentoService, ClienteService clienteService) {
        this.equipamentoService = equipamentoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long clienteId, Model model) {
        if (clienteId != null) {
            model.addAttribute("equipamentos", equipamentoService.listarPorCliente(clienteId));
            model.addAttribute("clienteFiltro", clienteService.buscarPorId(clienteId));
        } else {
            model.addAttribute("equipamentos", equipamentoService.listarTodos());
        }
        return "equipamentos/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long clienteId, Model model) {
        EquipamentoDTO dto = new EquipamentoDTO();
        if (clienteId != null) {
            dto.setClienteId(clienteId);
        }
        model.addAttribute("equipamento", dto);
        model.addAttribute("clientes", clienteService.listarTodos());
        return "equipamentos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Equipamento eq = equipamentoService.buscarPorId(id);
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setId(eq.getId());
        dto.setTipo(eq.getTipo());
        dto.setMarca(eq.getMarca());
        dto.setModelo(eq.getModelo());
        dto.setNumeroSerie(eq.getNumeroSerie());
        if (eq.getCliente() != null) {
            dto.setClienteId(eq.getCliente().getId());
        }

        model.addAttribute("equipamento", dto);
        model.addAttribute("clientes", clienteService.listarTodos());
        return "equipamentos/formulario";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("equipamento") EquipamentoDTO dto,
                         BindingResult erros,
                         Model model,
                         RedirectAttributes flash) {
        if (erros.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            return "equipamentos/formulario";
        }

        Equipamento equipamento = new Equipamento();
        equipamento.setId(dto.getId());
        equipamento.setTipo(dto.getTipo().trim());
        equipamento.setMarca(dto.getMarca().trim());
        equipamento.setModelo(dto.getModelo().trim());
        equipamento.setNumeroSerie(dto.getNumeroSerie() != null ? dto.getNumeroSerie().trim() : null);

        Cliente cliente = new Cliente();
        cliente.setId(dto.getClienteId());
        equipamento.setCliente(cliente);

        if (dto.getId() == null) {
            equipamentoService.salvar(equipamento);
            flash.addFlashAttribute("sucesso", "Equipamento cadastrado com sucesso!");
        } else {
            equipamentoService.atualizar(dto.getId(), equipamento);
            flash.addFlashAttribute("sucesso", "Equipamento atualizado com sucesso!");
        }

        return "redirect:/equipamentos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            equipamentoService.excluir(id);
            flash.addFlashAttribute("sucesso", "Equipamento excluído com sucesso!");
        } catch (Exception e) {
            flash.addFlashAttribute("erro", "Não foi possível excluir o equipamento: " + e.getMessage());
        }
        return "redirect:/equipamentos";
    }
}
