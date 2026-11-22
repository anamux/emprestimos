package com.projeto.emprestimo.exception;


import com.projeto.emprestimo.dto.EmprestimoRequest;
import com.projeto.emprestimo.model.Colaborador;
import com.projeto.emprestimo.model.Emprestimo;
import com.projeto.emprestimo.model.Equipamento;
import com.projeto.emprestimo.repository.EmprestimoRepository;

import com.projeto.emprestimo.service.ColaboradorService;
import com.projeto.emprestimo.service.EmprestimoService;
import com.projeto.emprestimo.service.EquipamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoServiceTest {

    private EmprestimoService service;
    private RepositorioFake repository;
    private EquipamentoServiceFake equipamentoService;
    private ColaboradorServiceFake colaboradorService;

    private Equipamento equipamento;
    private Colaborador colaborador;

    @BeforeEach
    void setUp() {

        repository = new RepositorioFake();
        equipamentoService = new EquipamentoServiceFake();
        colaboradorService = new ColaboradorServiceFake();

        service = new EmprestimoService(
                repository.criar(),
                equipamentoService,
                colaboradorService
        );

        equipamento = new Equipamento();
        equipamento.setId(1L);
        equipamento.setNome("Notebook");
        equipamento.setDisponivel(true);

        colaborador = new Colaborador();
        colaborador.setId(2L);
        colaborador.setNome("Ana");
        colaborador.setEmail("ana@email.com");
        colaborador.setCargo("Professora");

        equipamentoService.adicionar(equipamento);
        colaboradorService.adicionar(colaborador);
    }

    // 1 - Emprestar equipamento disponível
    @Test
    void deveEmprestarEquipamentoDisponivel() {

        EmprestimoRequest pedido = new EmprestimoRequest(1L, 2L);

        Emprestimo resultado = service.emprestar(pedido);

        assertNotNull(resultado);
        assertFalse(equipamento.isDisponivel());
        assertEquals(equipamento, resultado.getEquipamento());
        assertEquals(colaborador, resultado.getColaborador());
    }

    // 2 - R4: Emprestar equipamento indisponível
    @Test
    void deveLancarExcecaoAoEmprestarEquipamentoIndisponivel() {

        equipamento.setDisponivel(false);

        EmprestimoRequest pedido = new EmprestimoRequest(1L, 2L);

        assertThrows(
                ConflictException.class,
                () -> service.emprestar(pedido)
        );

        assertEquals(0, repository.quantidade());
    }

    // 3 - R5: Criar empréstimo com prazo de 14 dias
    @Test
    void deveCriarEmprestimoComPrazoDe14Dias() {

        LocalDate hoje = LocalDate.now();

        EmprestimoRequest pedido = new EmprestimoRequest(1L, 2L);

        Emprestimo resultado = service.emprestar(pedido);

        assertEquals(hoje, resultado.getDataEmprestimo());

        assertEquals(
                hoje.plusDays(14),
                resultado.getDataPrevistaDevolucao()
        );
    }

    // 4 - Devolver equipamento: fica disponível
    @Test
    void deveDeixarEquipamentoDisponivelAoDevolver() {

        equipamento.setDisponivel(false);

        Emprestimo emprestimo = criarEmprestimo(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                null
        );

        repository.salvar(emprestimo);

        service.devolver(emprestimo.getId());

        assertTrue(equipamento.isDisponivel());
    }

    // 5 - R7: Registrar data da devolução
    @Test
    void deveRegistrarDataDeDevolucao() {

        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = criarEmprestimo(
                hoje.minusDays(14),
                hoje,
                null
        );

        repository.salvar(emprestimo);

        Emprestimo resultado = service.devolver(emprestimo.getId());

        assertEquals(hoje, resultado.getDataDevolucao());
    }

    // 6 - R6: Não permitir devolver duas vezes
    @Test
    void deveLancarExcecaoAoDevolverDuasVezes() {

        Emprestimo emprestimo = criarEmprestimo(
                LocalDate.now().minusDays(14),
                LocalDate.now(),
                LocalDate.now()
        );

        repository.salvar(emprestimo);

        assertThrows(
                ConflictException.class,
                () -> service.devolver(emprestimo.getId())
        );
    }

    // 7 - R8: Empréstimo atrasado
    @Test
    void deveIdentificarEmprestimoAtrasado() {

        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = criarEmprestimo(
                hoje.minusDays(28),
                hoje.minusDays(14),
                null
        );

        repository.salvar(emprestimo);

        List<Emprestimo> resultado = service.listar(true);

        assertEquals(1, resultado.size());
        assertEquals(emprestimo.getId(), resultado.get(0).getId());
    }

    // 8 - Empréstimo com prazo futuro: não atrasado
    @Test
    void naoDeveConsiderarEmprestimoAtrasadoAntesDoPrazo() {

        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = criarEmprestimo(
                hoje.minusDays(8),
                hoje.plusDays(6),
                null
        );

        repository.salvar(emprestimo);

        List<Emprestimo> resultado = service.listar(true);

        assertTrue(resultado.isEmpty());
    }

    // 9 - Empréstimo devolvido: não atrasado
    @Test
    void naoDeveConsiderarEmprestimoDevolvidoComoAtrasado() {

        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = criarEmprestimo(
                hoje.minusDays(18),
                hoje.minusDays(4),
                hoje.minusDays(2)
        );

        repository.salvar(emprestimo);

        List<Emprestimo> resultado = service.listar(true);

        assertTrue(resultado.isEmpty());
    }

    // 10 - R8: No dia do prazo ainda não está atrasado
    @Test
    void naoDeveConsiderarAtrasadoNoDiaDoPrazo() {

        LocalDate hoje = LocalDate.now();

        Emprestimo emprestimo = criarEmprestimo(
                hoje.minusDays(14),
                hoje,
                null
        );

        repository.salvar(emprestimo);

        List<Emprestimo> resultado = service.listar(true);

        assertTrue(resultado.isEmpty());
    }

    // Método auxiliar para criar empréstimos
    private Emprestimo criarEmprestimo(
            LocalDate dataEmprestimo,
            LocalDate dataPrevista,
            LocalDate dataDevolucao) {

        Emprestimo emprestimo = new Emprestimo();

        emprestimo.setEquipamento(equipamento);
        emprestimo.setColaborador(colaborador);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataPrevistaDevolucao(dataPrevista);
        emprestimo.setDataDevolucao(dataDevolucao);

        return emprestimo;
    }

    // Repositório auxiliar em memória
    private static class RepositorioFake {

        private final Map<Long, Emprestimo> dados = new LinkedHashMap<>();
        private long proximoId = 1L;

        void salvar(Emprestimo emprestimo) {
            if (emprestimo.getId() == null) {
                emprestimo.setId(proximoId++);
            }

            dados.put(emprestimo.getId(), emprestimo);
        }

        int quantidade() {
            return dados.size();
        }

        EmprestimoRepository criar() {

            return (EmprestimoRepository) Proxy.newProxyInstance(
                    EmprestimoRepository.class.getClassLoader(),
                    new Class<?>[]{EmprestimoRepository.class},
                    (proxy, method, args) -> {

                        switch (method.getName()) {

                            case "save":
                                Emprestimo emprestimo =
                                        (Emprestimo) args[0];
                                salvar(emprestimo);
                                return emprestimo;

                            case "findById":
                                return Optional.ofNullable(
                                        dados.get(args[0])
                                );

                            case "findAll":
                                return new ArrayList<>(dados.values());

                            case "findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore":
                                LocalDate data = (LocalDate) args[0];

                                return dados.values().stream()
                                        .filter(e -> e.getDataDevolucao() == null)
                                        .filter(e -> e.getDataPrevistaDevolucao()
                                                .isBefore(data))
                                        .toList();

                            case "findByColaborador":
                                Colaborador c = (Colaborador) args[0];

                                return dados.values().stream()
                                        .filter(e -> e.getColaborador().equals(c))
                                        .toList();

                            case "existsByEquipamento":
                                Long equipamentoId = (Long) args[0];

                                return dados.values().stream()
                                        .anyMatch(e -> e.getEquipamento()
                                                .getId().equals(equipamentoId));

                            case "toString":
                                return "RepositorioFake";

                            case "hashCode":
                                return System.identityHashCode(proxy);

                            case "equals":
                                return proxy == args[0];

                            default:
                                throw new UnsupportedOperationException(
                                        "Método não implementado: " +
                                                method.getName()
                                );
                        }
                    }
            );
        }
    }

    private static class EquipamentoServiceFake
            extends EquipamentoService {

        private final Map<Long, Equipamento> equipamentos =
                new HashMap<>();

        EquipamentoServiceFake() {
            super(null, null);
        }

        void adicionar(Equipamento equipamento) {
            equipamentos.put(equipamento.getId(), equipamento);
        }

        @Override
        public Equipamento buscar(Long id) {
            Equipamento equipamento = equipamentos.get(id);

            if (equipamento == null) {
                throw new IllegalArgumentException(
                        "Equipamento não encontrado"
                );
            }

            return equipamento;
        }
    }

    // Serviço auxiliar de colaboradores
    private static class ColaboradorServiceFake
            extends ColaboradorService {

        private final Map<Long, Colaborador> colaboradores =
                new HashMap<>();

        ColaboradorServiceFake() {
            super(null);
        }

        void adicionar(Colaborador colaborador) {
            colaboradores.put(colaborador.getId(), colaborador);
        }

        @Override
        public Colaborador buscar(Long id) {
            Colaborador colaborador = colaboradores.get(id);

            if (colaborador == null) {
                throw new IllegalArgumentException(
                        "Colaborador não encontrado"
                );
            }

            return colaborador;
        }
    }
}
