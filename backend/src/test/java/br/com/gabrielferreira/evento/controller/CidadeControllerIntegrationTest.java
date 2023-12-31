package br.com.gabrielferreira.evento.controller;

import br.com.gabrielferreira.evento.dto.request.CidadeRequestDTO;
import br.com.gabrielferreira.evento.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static br.com.gabrielferreira.evento.tests.CidadeFactory.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CidadeControllerIntegrationTest {

    private static final String URL = "/cidades";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON;
    private static final String NOME_REPETIDO = "Rio de Janeiro";
    private static final String CODIGO_REPETIDO = "RIO_DE_JANEIRO";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TokenUtils tokenUtils;

    private Long idCidadeExistente;

    private Long idCidadeInexistente;

    private CidadeRequestDTO cidadeRequestDTO;

    private CidadeRequestDTO cidadeUpdateDTO;

    private Long idCidadeSemRelacionamento;

    private String tokenAdmin;

    private String tokenClient;

    @BeforeEach
    void setUp(){
        idCidadeExistente = 1L;
        idCidadeInexistente = -1L;
        idCidadeSemRelacionamento = 5L;

        cidadeRequestDTO = criarCidadeInsertDto();
        cidadeUpdateDTO = criarCidadeUpdateDto();

        tokenAdmin = tokenUtils.gerarToken(mockMvc, "teste@email.com", "123");
        tokenClient = tokenUtils.gerarToken(mockMvc, "gabriel123@email.com", "123");
    }

    @Test
    @DisplayName("Deve buscar lista de cidades")
    @Order(1)
    void deveBuscarListaDeCidades() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar cidade quando existir")
    @Order(2)
    void deveBuscarCidade() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/{id}"), idCidadeExistente)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.nome").exists());
        resultActions.andExpect(jsonPath("$.codigo").exists());
        resultActions.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Não deve buscar cidade quando não existir")
    @Order(3)
    void naoDeveBuscarCidade() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/{id}"), idCidadeInexistente)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isNotFound());
        resultActions.andExpect(jsonPath("$.mensagem").value("Cidade não encontrada"));
    }

    @Test
    @DisplayName("Deve buscar cidade quando informar o código")
    @Order(4)
    void deveBuscarCidadeQuandoInformarCodigo() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/buscar?codigo=SAO_PAULO"))
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.nome").exists());
        resultActions.andExpect(jsonPath("$.codigo").exists());
        resultActions.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Não deve buscar cidade quando não informar o código")
    @Order(5)
    void naoDeveBuscarCidadeQuandoNaoInformarCodigo() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/buscar?codigo="))
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.mensagem").value("É necessário informar o código"));
    }

    @Test
    @DisplayName("Não deve buscar cidade quando informar o código inválido")
    @Order(6)
    void naoDeveBuscarCidadeQuandoInformarCodigoInvalido() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/buscar?codigo=invalido"))
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isNotFound());
        resultActions.andExpect(jsonPath("$.mensagem").value("Cidade não encontrada"));
    }

    @Test
    @DisplayName("Não deve buscar cidade quando não informar o código como parametro")
    @Order(7)
    void naoDeveBuscarCidadeQuandoNaoInformarCodigoComoParametro() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get(URL.concat("/buscar"))
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isInternalServerError());
        resultActions.andExpect(jsonPath("$.mensagem").value("Parâmetro requerido 'codigo' do tipo 'String' não está presente"));
    }

    @Test
    @DisplayName("Deve cadastrar uma cidade")
    @Order(8)
    void deveCadastrarCidade() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        String nomeEsperado = cidadeRequestDTO.getNome();
        String codigoEsperado = cidadeRequestDTO.getCodigo();

        ResultActions resultActions = mockMvc
                .perform(post(URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.nome").value(nomeEsperado));
        resultActions.andExpect(jsonPath("$.codigo").value(codigoEsperado));
        resultActions.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Não deve cadastrar uma cidade quando o nome já estiver cadastrado")
    @Order(9)
    void naoDeveCadastrarCidadeQuandoNomeJaEstiverCadastrado() throws Exception{
        cidadeRequestDTO.setNome("São Paulo");
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(post(URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível cadastrar esta cidade pois o nome 'São Paulo' já foi cadastrado"));
    }

    @Test
    @DisplayName("Não deve cadastrar uma cidade quando o código estiver espaço")
    @Order(10)
    void naoDeveCadastrarCidadeQuandoCodigoEstiverEspaco() throws Exception{
        cidadeRequestDTO.setCodigo("RIO DE JANEIRO");
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(post(URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível cadastrar esta cidade pois o código 'RIO DE JANEIRO' possui espaço em branco"));
    }

    @Test
    @DisplayName("Não deve cadastrar uma cidade quando o código não estiver toda maiúscula")
    @Order(11)
    void naoDeveCadastrarCidadeQuandoCodigoNaoEstiverMaiuscula() throws Exception{
        cidadeRequestDTO.setCodigo("rio_de_JANEIRO");
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(post(URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível cadastrar esta cidade pois o código 'rio_de_JANEIRO' tem que ser toda maiúsculas"));
    }

    @Test
    @DisplayName("Não deve cadastrar uma cidade quando o código já estiver cadastrado")
    @Order(12)
    void naoDeveCadastrarCidadeQuandoCodigoJaEstiverCadastrado() throws Exception{
        cidadeRequestDTO.setCodigo(CODIGO_REPETIDO);
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(post(URL)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível cadastrar esta cidade pois o código 'RIO_DE_JANEIRO' já foi cadastrado"));
    }

    @Test
    @DisplayName("Não deve atualizar uma cidade quando o nome já estiver cadastrado")
    @Order(13)
    void naoDeveAtualizarCidadeQuandoNomeJaEstiverCadastrado() throws Exception{
        cidadeRequestDTO.setNome(NOME_REPETIDO);
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(put(URL.concat("/{id}"), idCidadeExistente)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível atualizar esta cidade pois o nome 'Rio de Janeiro' já foi cadastrado"));
    }

    @Test
    @DisplayName("Não deve atualizar uma cidade quando o código já estiver cadastrado")
    @Order(14)
    void naoDeveAtualizarCidadeQuandoCodigoJaEstiverCadastrado() throws Exception{
        cidadeRequestDTO.setCodigo(CODIGO_REPETIDO);
        String jsonBody = objectMapper.writeValueAsString(cidadeRequestDTO);

        ResultActions resultActions = mockMvc
                .perform(put(URL.concat("/{id}"), idCidadeExistente)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Não vai ser possível atualizar esta cidade pois o código 'RIO_DE_JANEIRO' já foi cadastrado"));
    }

    @Test
    @DisplayName("Deve atualizar cidade quando informar valores corretos")
    @Order(15)
    void deveAtualizarCidade() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(cidadeUpdateDTO);

        Long idEsperado = idCidadeExistente;
        String nomeEsperado = cidadeUpdateDTO.getNome();
        String codigoEsperado = cidadeUpdateDTO.getCodigo();

        ResultActions resultActions = mockMvc
                .perform(put(URL.concat("/{id}"), idCidadeExistente)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(idEsperado));
        resultActions.andExpect(jsonPath("$.nome").value(nomeEsperado));
        resultActions.andExpect(jsonPath("$.codigo").value(codigoEsperado));
        resultActions.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Deve deletar cidade quando existir")
    @Order(16)
    void deveDeletarCidade() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(delete(URL.concat("/{id}"), idCidadeSemRelacionamento)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Não deve deletar cidade quando tiver relacionamento com outra tabela")
    @Order(17)
    @Transactional(propagation = Propagation.NEVER)
    void naoDeveDeletarCidadeQuandoCidadeTiverRelacionamento() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(delete(URL.concat("/{id}"), idCidadeExistente)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.erro").value("Erro personalizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Violação de integridade, Cidade possui algum tipo de relacionamento"));
    }

    @Test
    @DisplayName("Não deve deletar cidade quando não tiver autenticado")
    @Order(18)
    void naoDeveDeletarCidadeQuandoNaoTiverAutenticado() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(delete(URL.concat("/{id}"), idCidadeSemRelacionamento)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("$.erro").value("Não autorizado"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Você precisa fazer login primeiro para executar esta função"));
    }

    @Test
    @DisplayName("Não deve deletar cidade quando for cliente")
    @Order(19)
    void naoDeveDeletarCidadeQuandoForCliente() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(delete(URL.concat("/{id}"), idCidadeSemRelacionamento)
                        .header("Authorization", "Bearer " + tokenClient)
                        .accept(MEDIA_TYPE_JSON));

        resultActions.andExpect(status().isForbidden());
        resultActions.andExpect(jsonPath("$.erro").value("Proibido"));
        resultActions.andExpect(jsonPath("$.mensagem").value("Você não tem a permissão de realizar esta ação"));
    }
}
