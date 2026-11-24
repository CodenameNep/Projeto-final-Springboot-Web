package com.assistencia.controller;

import com.assistencia.dto.ClienteDTO;
import com.assistencia.model.Cliente;
import com.assistencia.model.Endereco;
import com.assistencia.service.ClienteService;
import com.assistencia.service.ViaCepService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ViaCepService viaCepService;

    public ClienteController(ClienteService clienteService, ViaCepService viaCepService) {
        this.clienteService = clienteService;
        this.viaCepService = viaCepService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new ClienteDTO());
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());

        if (cliente.getEndereco() != null) {
            Endereco end = cliente.getEndereco();
            dto.setCep(end.getCep());
            dto.setLogradouro(end.getLogradouro());
            dto.setNumero(end.getNumero());
            dto.setComplemento(end.getComplemento());
            dto.setBairro(end.getBairro());
            dto.setCidade(end.getCidade());
            dto.setUf(end.getUf());
        }

        model.addAttribute("cliente", dto);
        return "clientes/formulario";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("cliente") ClienteDTO dto,
                         BindingResult erros,
                         RedirectAttributes flash) {
        if (erros.hasErrors()) {
            return "clientes/formulario";
        }

        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNome(dto.getNome().trim());
        cliente.setTelefone(dto.getTelefone().trim());
        cliente.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);

        Endereco endereco = new Endereco();
        endereco.setCep(dto.getCep().trim());
        endereco.setLogradouro(dto.getLogradouro().trim());
        endereco.setNumero(dto.getNumero().trim());
        endereco.setComplemento(dto.getComplemento() != null ? dto.getComplemento().trim() : null);
        endereco.setBairro(dto.getBairro().trim());
        endereco.setCidade(dto.getCidade().trim());
        endereco.setUf(dto.getUf().trim().toUpperCase());

        cliente.setEndereco(endereco);

        if (dto.getId() == null) {
            clienteService.salvar(cliente);
            flash.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso!");
        } else {
            clienteService.atualizar(dto.getId(), cliente);
            flash.addFlashAttribute("sucesso", "Cliente atualizado com sucesso!");
        }

        return "redirect:/clientes";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            clienteService.excluir(id);
            flash.addFlashAttribute("sucesso", "Cliente excluído com sucesso!");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/clientes";
    }

    // Endpoint REST para busca dinâmica de CEP via AJAX no frontend
    @GetMapping("/cep/{cep}")
    @ResponseBody
    public ResponseEntity<?> buscarEnderecoPorCep(@PathVariable String cep) {
        try {
            Endereco endereco = viaCepService.buscarEnderecoPorCep(cep);
            return ResponseEntity.ok(endereco);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
