package br.com.portfolio.helpdesk.service;

import br.com.portfolio.helpdesk.dto.ChamadoRequest;
import br.com.portfolio.helpdesk.dto.ChamadoResponse;
import br.com.portfolio.helpdesk.exception.RecursoNaoEncontradoException;
import br.com.portfolio.helpdesk.model.CategoriaChamado;
import br.com.portfolio.helpdesk.model.Chamado;
import br.com.portfolio.helpdesk.model.Prioridade;
import br.com.portfolio.helpdesk.model.StatusChamado;
import br.com.portfolio.helpdesk.model.Usuario;
import br.com.portfolio.helpdesk.repository.ChamadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ChamadoService chamadoService;

    private ChamadoRequest requestValido() {
        return new ChamadoRequest(
                1L,
                "Não consigo acessar o sistema",
                "Ao tentar entrar, aparece uma mensagem de acesso negado.",
                CategoriaChamado.ACESSO,
                Prioridade.ALTA
        );
    }

    @Test
    void abrirChamadoParaUsuarioExistenteRetornaStatusAberto() {
        Usuario usuario = new Usuario("Maria Silva", "maria@example.com");
        when(usuarioService.buscarEntidade(1L)).thenReturn(usuario);
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ChamadoResponse response = chamadoService.abrir(requestValido());

        assertEquals(StatusChamado.ABERTO, response.status());
        assertEquals("Maria Silva", response.usuario().nome());
    }

    @Test
    void abrirChamadoParaUsuarioInexistenteLancaNaoEncontrado() {
        when(usuarioService.buscarEntidade(1L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado com id 1"));

        assertThrows(RecursoNaoEncontradoException.class, () -> chamadoService.abrir(requestValido()));
    }
}
