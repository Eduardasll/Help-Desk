package br.com.portfolio.helpdesk.service;

import br.com.portfolio.helpdesk.dto.UsuarioRequest;
import br.com.portfolio.helpdesk.dto.UsuarioResponse;
import br.com.portfolio.helpdesk.exception.EmailJaCadastradoException;
import br.com.portfolio.helpdesk.exception.RecursoNaoEncontradoException;
import br.com.portfolio.helpdesk.model.Usuario;
import br.com.portfolio.helpdesk.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário com o e-mail " + request.email());
        }
        Usuario usuario = new Usuario(request.nome(), request.email());
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponse.de(salvo);
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado com id " + id));
    }
}
