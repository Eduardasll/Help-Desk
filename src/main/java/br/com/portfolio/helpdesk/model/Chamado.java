package br.com.portfolio.helpdesk.model;

import br.com.portfolio.helpdesk.exception.TransicaoStatusInvalidaException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaChamado categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado status;

    @Column(nullable = false)
    private LocalDateTime abertoEm;

    private LocalDateTime resolvidoEm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected Chamado() {
    }

    public Chamado(String titulo, String descricao, CategoriaChamado categoria,
                   Prioridade prioridade, Usuario usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.usuario = usuario;
        this.status = StatusChamado.ABERTO;
        this.abertoEm = LocalDateTime.now();
        this.resolvidoEm = null;
    }

    public void atualizarDados(String titulo, String descricao,
                              CategoriaChamado categoria, Prioridade prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
    }

    public void iniciarAtendimento() {
        if (this.status != StatusChamado.ABERTO) {
            throw new TransicaoStatusInvalidaException(
                    "Só é possível iniciar um chamado que está ABERTO");
        }
        this.status = StatusChamado.EM_ANDAMENTO;
    }

    public void resolver() {
        if (this.status != StatusChamado.EM_ANDAMENTO) {
            throw new TransicaoStatusInvalidaException(
                    "Só é possível resolver um chamado que está EM_ANDAMENTO");
        }
        this.status = StatusChamado.RESOLVIDO;
        this.resolvidoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public CategoriaChamado getCategoria() {
        return categoria;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public StatusChamado getStatus() {
        return status;
    }

    public LocalDateTime getAbertoEm() {
        return abertoEm;
    }

    public LocalDateTime getResolvidoEm() {
        return resolvidoEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
