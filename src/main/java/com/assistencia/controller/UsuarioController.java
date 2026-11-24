package com.assistencia.controller;

import com.assistencia.dto.UsuarioCadastroDTO;
import com.assistencia.model.Usuario;
import com.assistencia.model.enums.Perfil;
import com.assistencia.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute("perfis")
    public Perfil[] perfis() {
        return Perfil.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new UsuarioCadastroDTO());
        return "usuarios/formulario";
    }

    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("usuario") UsuarioCadastroDTO dados,
                            BindingResult erros, RedirectAttributes flash) {
        if (erros.hasErrors()) {
            return "usuarios/formulario";
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.getNome().trim());
        usuario.setEmail(dados.getEmail().trim().toLowerCase(Locale.ROOT));
        usuario.setSenha(dados.getSenha());
        usuario.setPerfil(dados.getPerfil());
        usuario.setAtivo(true);

        try {
            // UsuarioService ja utiliza BCrypt atraves do PasswordEncoder.
            usuarioService.salvar(usuario, passwordEncoder);
        } catch (IllegalArgumentException e) {
            erros.rejectValue("email", "email.duplicado", e.getMessage());
            return "usuarios/formulario";
        }

        flash.addFlashAttribute("sucesso", "Usuario cadastrado com sucesso!");
        return "redirect:/usuarios";
    }
}
