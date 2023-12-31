package br.com.gabrielferreira.evento.service;

import br.com.gabrielferreira.evento.domain.EventoDomain;
import br.com.gabrielferreira.evento.entity.Evento;
import br.com.gabrielferreira.evento.exception.NaoEncontradoException;
import br.com.gabrielferreira.evento.repository.EventoRepository;
import br.com.gabrielferreira.evento.service.validation.EventoValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static br.com.gabrielferreira.evento.tests.EventoFactory.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventoServiceTest {

    @InjectMocks
    private EventoService eventoService;

    @Mock
    private CidadeService cidadeService;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private EventoValidator eventoValidator;

    private EventoDomain eventoInsertDomain;

    private Evento eventoInsert;

    private Evento eventoUpdate;

    private Long idEventoExistente;

    private Long idEventoInexistente;

    private EventoDomain eventoUpdateDomain;

    @BeforeEach
    void setUp(){
        idEventoExistente = 1L;
        idEventoInexistente = -1L;
        eventoInsertDomain = criarEventoDomainInsert(criarEventoInsertDto());
        eventoUpdateDomain = criarEventoDomainUpdate(idEventoExistente, criarEventoUpdateDto());

        eventoInsert = criarEventoInsert(eventoInsertDomain);
        eventoUpdate = criarEventoUpdate(eventoUpdateDomain);
    }

    @Test
    @DisplayName("Deve cadastrar evento quando informar valores corretos")
    @Order(1)
    void deveCadastrarEvento(){
        doNothing().when(eventoValidator).validarCampos(eventoInsertDomain);
        doNothing().when(eventoValidator).validarNome(eventoInsertDomain);
        doNothing().when(eventoValidator).validarCidade(eventoInsertDomain);
        when(cidadeService.buscarCidadePorId(eventoInsertDomain.getCidade().getId())).thenReturn(gerarCidadeDomain());
        when(eventoRepository.save(any())).thenReturn(eventoInsert);

        EventoDomain eventoDomain = eventoService.cadastrarEvento(eventoInsertDomain);

        assertNotNull(eventoDomain);
        verify(eventoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve buscar evento por id quando existir")
    @Order(2)
    void deveBuscarEventoPorId() {
        when(eventoRepository.buscarEventoPorId(idEventoExistente)).thenReturn(Optional.of(eventoInsert));

        EventoDomain eventoDomain = eventoService.buscarEventoPorId(idEventoExistente);

        assertNotNull(eventoDomain);
        verify(eventoRepository, times(1)).buscarEventoPorId(idEventoExistente);
    }

    @Test
    @DisplayName("Nao deve buscar evento por id quando não existir")
    @Order(3)
    void naoDeveBuscarEventoPorId() {
        when(eventoRepository.buscarEventoPorId(idEventoInexistente)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> eventoService.buscarEventoPorId(idEventoInexistente));
        verify(eventoRepository, times(1)).buscarEventoPorId(idEventoInexistente);
    }

    @Test
    @DisplayName("Deve atualizar evento quando informar valores corretos")
    @Order(4)
    void deveAtualizarEventos() {
        doNothing().when(eventoValidator).validarCampos(eventoUpdateDomain);
        doNothing().when(eventoValidator).validarNome(eventoUpdateDomain);
        doNothing().when(eventoValidator).validarCidade(eventoUpdateDomain);
        when(eventoRepository.buscarEventoPorId(idEventoExistente)).thenReturn(Optional.of(eventoInsert));
        when(cidadeService.buscarCidadePorId(eventoUpdateDomain.getCidade().getId())).thenReturn(gerarCidadeDomain2());
        when(eventoRepository.save(any())).thenReturn(eventoUpdate);

        EventoDomain eventoDomain = eventoService.atualizarEvento(eventoUpdateDomain);

        assertNotNull(eventoDomain);
        verify(eventoRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Deve deletar evento quando informar id existente")
    @Order(6)
    void deveDeletarEvento() {
        when(eventoRepository.buscarEventoPorId(idEventoExistente)).thenReturn(Optional.of(eventoInsert));
        doNothing().when(eventoRepository).deleteById(eventoInsert.getId());

        assertDoesNotThrow(() -> eventoService.deletarEventoPorId(idEventoExistente));
        verify(eventoRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("Não deve deletar evento quando informar id inexistente")
    @Order(7)
    void naoDeveDeletarEvento() {
        when(eventoRepository.buscarEventoPorId(idEventoInexistente)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> eventoService.deletarEventoPorId(idEventoInexistente));
        verify(eventoRepository, never()).deleteById(any());
    }
}
