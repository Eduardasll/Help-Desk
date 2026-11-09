package br.com.portfolio.helpdesk.dto;

import br.com.portfolio.helpdesk.model.CategoriaChamado;
import br.com.portfolio.helpdesk.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoUpdateRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(min = 5, max = 120, message = "O título deve ter de 5 a 120 caracteres")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 10, max = 1000, message = "A descrição deve ter de 10 a 1000 caracteres")
        String descricao,

        @NotNull(message = "A categoria é obrigatória")
        CategoriaChamado categoria,

        @NotNull(message = "A prioridade é obrigatória")
        Prioridade prioridade
) {
}
