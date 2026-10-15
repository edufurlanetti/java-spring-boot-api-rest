package br.edu.furlanetti.manutencao.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração: sobem a aplicação inteira com o H2 e os dados do data.sql
 * e exercitam a API pela camada HTTP. @Transactional desfaz as alterações ao fim de cada teste.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegracaoTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /api/equipamentos lista apenas os ativos")
    void listaEquipamentosAtivos() throws Exception {
        mvc.perform(get("/api/equipamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @DisplayName("POST /api/equipamentos devolve 201, Location e código normalizado")
    void cadastraEquipamento() throws Exception {
        String json = """
                {"codigo": " esteira-01 ", "nome": "Esteira transportadora", "setor": "Expedição", "fabricante": "Flexlink"}
                """;

        mvc.perform(post("/api/equipamentos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/api/equipamentos/\\d+")))
                .andExpect(jsonPath("$.codigo").value("ESTEIRA-01"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Código duplicado gera 422")
    void codigoDuplicado() throws Exception {
        String json = """
                {"codigo": "TORNO-01", "nome": "Outro torno", "setor": "Usinagem"}
                """;

        mvc.perform(post("/api/equipamentos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"));
    }

    @Test
    @DisplayName("Campos obrigatórios em branco geram 400 com a lista de campos")
    void validacaoDeCampos() throws Exception {
        String json = """
                {"codigo": "X-1", "nome": "", "setor": ""}
                """;

        mvc.perform(post("/api/equipamentos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.setor").exists());
    }

    @Test
    @DisplayName("Ciclo de vida completo de uma ordem de serviço")
    void cicloDeVida() throws Exception {
        String abrir = """
                {"equipamentoId": 2, "descricao": "Folga no carro transversal", "tipo": "CORRETIVA", "prioridade": "ALTA"}
                """;
        MvcResult resultado = mvc.perform(post("/api/ordens").contentType(MediaType.APPLICATION_JSON).content(abrir))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"))
                .andExpect(jsonPath("$.equipamentoCodigo").value("TORNO-02"))
                .andReturn();

        String local = resultado.getResponse().getHeader("Location");
        String id = local.substring(local.lastIndexOf('/') + 1);

        mvc.perform(patch("/api/ordens/" + id + "/iniciar")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"tecnico\": \"Juliana Lopes\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        mvc.perform(patch("/api/ordens/" + id + "/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"solucao\": \"Ajuste das réguas\", \"horasTrabalhadas\": 3.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.horasTrabalhadas").value(3.5));
    }

    @Test
    @DisplayName("Concluir ordem ABERTA é transição inválida (422)")
    void transicaoInvalida() throws Exception {
        mvc.perform(patch("/api/ordens/6/concluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"solucao\": \"x\", \"horasTrabalhadas\": 1}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Não abre ordem para equipamento inativo (422)")
    void equipamentoInativo() throws Exception {
        String json = """
                {"equipamentoId": 6, "descricao": "Revisão", "tipo": "PREVENTIVA", "prioridade": "BAIXA"}
                """;

        mvc.perform(post("/api/ordens").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Filtro por status com paginação")
    void filtroPorStatus() throws Exception {
        mvc.perform(get("/api/ordens").param("status", "ABERTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].status").value("ABERTA"));
    }

    @Test
    @DisplayName("Recurso inexistente gera 404")
    void naoEncontrado() throws Exception {
        mvc.perform(get("/api/ordens/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Indicadores contam as ordens por status")
    void indicadores() throws Exception {
        mvc.perform(get("/api/ordens/indicadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordensPorStatus.CONCLUIDA").value(4))
                .andExpect(jsonPath("$.ordensPorStatus.ABERTA").value(2))
                .andExpect(jsonPath("$.horasPorEquipamento[0].equipamento").value("TORNO-01"));
    }
}
