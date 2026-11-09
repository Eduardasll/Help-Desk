package br.com.portfolio.helpdesk.repository;

import br.com.portfolio.helpdesk.model.Chamado;
import br.com.portfolio.helpdesk.model.Prioridade;
import br.com.portfolio.helpdesk.model.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    @Query("""
            SELECT c FROM Chamado c
            WHERE (:status IS NULL OR c.status = :status)
              AND (:prioridade IS NULL OR c.prioridade = :prioridade)
              AND (:usuarioId IS NULL OR c.usuario.id = :usuarioId)
            ORDER BY c.abertoEm DESC
            """)
    List<Chamado> buscarComFiltros(@Param("status") StatusChamado status,
                                   @Param("prioridade") Prioridade prioridade,
                                   @Param("usuarioId") Long usuarioId);
}
