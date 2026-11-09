package br.com.portfolio.helpdesk.service;

import br.com.portfolio.helpdesk.dto.ChamadoRequest;
import br.com.portfolio.helpdesk.dto.ChamadoResponse;
import br.com.portfolio.helpdesk.dto.ChamadoUpdateRequest;
import br.com.portfolio.helpdesk.exception.RecursoNaoEncontradoException;
import br.com.portfolio.helpdesk.model.Chamado;
import br.com.portfolio.helpdesk.model.Prioridade;
import br.com.portfolio.helpdesk.model.StatusChamado;
import br.com.portfolio.helpdesk.model.Usuario;
import br.com.portfolio.helpdesk.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioService usuarioService;

    public ChamadoService(ChamadoRepository chamadoRepository, UsuarioService usuarioService) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioService = usuarioService;
    }

    public ChamadoResponse abrir(ChamadoRequest request) {
        Usuario usuario = usuarioService.buscarEntidade(request.usuarioId());
        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.categoria(),
                request.prioridade(),
                usuario
        );
        return ChamadoResponse.de(chamadoRepository.save(chamado));
    }

    public List<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade, Long usuarioId) {
        return chamadoRepository.buscarComFiltros(status, prioridade, usuarioId).stream()
                .map(ChamadoResponse::de)
                .toList();
    }

    public ChamadoResponse buscarPorId(Long id) {
        return ChamadoResponse.de(buscarEntidade(id));
    }

    public ChamadoResponse atualizar(Long id, ChamadoUpdateRequest request) {
        Chamado chamado = buscarEntidade(id);
        chamado.atualizarDados(
                request.titulo(),
                request.descricao(),
                request.categoria(),
                request.prioridade()
        );
        return ChamadoResponse.de(chamadoRepository.save(chamado));
    }

    public ChamadoResponse iniciar(Long id) {
        Chamado chamado = buscarEntidade(id);
        chamado.iniciarAtendimento();
        return ChamadoResponse.de(chamadoRepository.save(chamado));
    }

    public ChamadoResponse resolver(Long id) {
        Chamado chamado = buscarEntidade(id);
        chamado.resolver();
        return ChamadoResponse.de(chamadoRepository.save(chamado));
    }

    private Chamado buscarEntidade(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado com id " + id));
    }
}
