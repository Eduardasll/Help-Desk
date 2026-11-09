package br.com.portfolio.helpdesk.dto;

import br.com.portfolio.helpdesk.model.CategoriaChamado;
import br.com.portfolio.helpdesk.model.Chamado;
import br.com.portfolio.helpdesk.model.Prioridade;
import br.com.portfolio.helpdesk.model.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        CategoriaChamado categoria,
        Prioridade prioridade,
        StatusChamado status,
        UsuarioResumo usuario,
        LocalDateTime abertoEm,
        LocalDateTime resolvidoEm
) {

    public record UsuarioResumo(Long id, String nome) {
    }

    public static ChamadoResponse de(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getCategoria(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                new UsuarioResumo(chamado.getUsuario().getId(), chamado.getUsuario().getNome()),
                chamado.getAbertoEm(),
                chamado.getResolvidoEm()
        );
    }
}
