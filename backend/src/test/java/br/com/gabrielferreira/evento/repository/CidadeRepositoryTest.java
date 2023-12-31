package br.com.gabrielferreira.evento.repository;

import br.com.gabrielferreira.evento.entity.Cidade;
import br.com.gabrielferreira.evento.repository.projection.CidadeProjection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CidadeRepositoryTest {

    @Autowired
    protected CidadeRepository cidadeRepository;

    @Test
    @DisplayName("Deve buscar lista de cidades")
    @Order(1)
    void deveBuscarListaDeCidades(){
        List<Cidade> cidades = cidadeRepository.buscarCidades();

        assertFalse(cidades.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar cidade por codigo quando informar")
    @Order(2)
    void deveBuscarCidadePorCodigo(){
        Optional<Cidade> cidade = cidadeRepository.buscarCidadePorCodigo("SAO_PAULO");

        assertTrue(cidade.isPresent());
    }

    @Test
    @DisplayName("Não deve buscar cidade por codigo quando informar código inválido")
    @Order(3)
    void naoDeveBuscarCidadePorCodigo(){
        Optional<Cidade> cidade = cidadeRepository.buscarCidadePorCodigo("teste123");

        assertFalse(cidade.isPresent());
    }

    @Test
    @DisplayName("Deve existe cidade quando informar o nome")
    @Order(4)
    void deveExistirCidadeQuandoInformarNome(){
        Optional<CidadeProjection> cidade = cidadeRepository.existeNomeCidade("São Paulo");

        assertTrue(cidade.isPresent());
    }

    @Test
    @DisplayName("Deve existe cidade quando informar o código")
    @Order(5)
    void deveExistirCidadeQuandoInformarCodigo(){
        Optional<CidadeProjection> cidade = cidadeRepository.existeCodigoCidade("SAO_PAULO");

        assertTrue(cidade.isPresent());
    }
}
