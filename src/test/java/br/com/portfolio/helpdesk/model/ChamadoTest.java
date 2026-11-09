package br.com.portfolio.helpdesk.model;

import br.com.portfolio.helpdesk.exception.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChamadoTest {

    private Chamado novoChamado() {
        Usuario usuario = new Usuario("Maria Silva", "maria@example.com");
        return new Chamado(
                "Não consigo acessar o sistema",
                "Ao tentar entrar, aparece uma mensagem de acesso negado.",
                CategoriaChamado.ACESSO,
                Prioridade.ALTA,
                usuario
        );
    }

    @Test
    void novoChamadoComecaAbertoESemDataDeResolucao() {
        Chamado chamado = novoChamado();

        assertEquals(StatusChamado.ABERTO, chamado.getStatus());
        assertNotNull(chamado.getAbertoEm());
        assertNull(chamado.getResolvidoEm());
    }

    @Test
    void iniciarAtendimentoMudaStatusParaEmAndamento() {
        Chamado chamado = novoChamado();

        chamado.iniciarAtendimento();

        assertEquals(StatusChamado.EM_ANDAMENTO, chamado.getStatus());
    }

    @Test
    void resolverChamadoEmAndamentoPreencheDataDeResolucao() {
        Chamado chamado = novoChamado();
        chamado.iniciarAtendimento();

        chamado.resolver();

        assertEquals(StatusChamado.RESOLVIDO, chamado.getStatus());
        assertNotNull(chamado.getResolvidoEm());
    }

    @Test
    void naoPermiteResolverChamadoAindaAberto() {
        Chamado chamado = novoChamado();

        assertThrows(TransicaoStatusInvalidaException.class, chamado::resolver);
    }

    @Test
    void naoPermiteIniciarChamadoJaResolvido() {
        Chamado chamado = novoChamado();
        chamado.iniciarAtendimento();
        chamado.resolver();

        assertThrows(TransicaoStatusInvalidaException.class, chamado::iniciarAtendimento);
    }
}
