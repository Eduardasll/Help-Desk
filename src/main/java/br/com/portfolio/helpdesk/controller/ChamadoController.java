package br.com.portfolio.helpdesk.controller;

import br.com.portfolio.helpdesk.dto.ChamadoRequest;
import br.com.portfolio.helpdesk.dto.ChamadoResponse;
import br.com.portfolio.helpdesk.dto.ChamadoUpdateRequest;
import br.com.portfolio.helpdesk.model.Prioridade;
import br.com.portfolio.helpdesk.model.StatusChamado;
import br.com.portfolio.helpdesk.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> abrir(@Valid @RequestBody ChamadoRequest request) {
        ChamadoResponse response = chamadoService.abrir(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ChamadoResponse> listar(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long usuarioId) {
        return chamadoService.listar(status, prioridade, usuarioId);
    }

    @GetMapping("/{id}")
    public ChamadoResponse buscarPorId(@PathVariable Long id) {
        return chamadoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ChamadoResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody ChamadoUpdateRequest request) {
        return chamadoService.atualizar(id, request);
    }

    @PatchMapping("/{id}/iniciar")
    public ChamadoResponse iniciar(@PathVariable Long id) {
        return chamadoService.iniciar(id);
    }

    @PatchMapping("/{id}/resolver")
    public ChamadoResponse resolver(@PathVariable Long id) {
        return chamadoService.resolver(id);
    }
}
