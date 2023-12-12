
package com.antonio.hundirlaflota.Controladores;

import com.antonio.hundirlaflota.Modelos.Usuario;
import com.antonio.hundirlaflota.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "${frontend.url}")
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Value("${frontend.url}")
    String frontendUrl;

    @RequestMapping("/user")
    public Usuario user(@AuthenticationPrincipal OAuth2User principal) {
        Usuario usuario = new Usuario();
        
        if (principal != null) {
            System.out.println(principal);
            String name = principal.getAttribute("name");
            String email = principal.getAttribute("email");
            
            // Check if name and email are not null before setting them
            if (name != null) {
                usuario.setNombre(name);
            }
            if (email != null) {
                usuario.setEmail(email);
            }
            
            usuarioRepository.save(usuario);
            System.out.println("usuario" + usuario);
        } else {
            // Handle the case where principal is null (optional based on your requirements)
            // For example:
            throw new RuntimeException("Principal is null"); // You can customize this exception or handle it differently
        }
        
        return usuario;
    }
  

}
