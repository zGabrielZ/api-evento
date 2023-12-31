package br.com.gabrielferreira.evento.tests;

import br.com.gabrielferreira.evento.domain.CidadeDomain;
import br.com.gabrielferreira.evento.domain.EventoDomain;
import br.com.gabrielferreira.evento.dto.request.CidadeIdRequestDTO;
import br.com.gabrielferreira.evento.dto.request.EventoRequestDTO;
import br.com.gabrielferreira.evento.entity.Cidade;
import br.com.gabrielferreira.evento.entity.Evento;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static br.com.gabrielferreira.evento.utils.DataUtils.UTC;
import static br.com.gabrielferreira.evento.tests.CidadeFactory.*;

public class EventoFactory {

    private EventoFactory(){}

    public static CidadeDomain gerarCidadeDomain(){
        Cidade cidade = gerarCidade();
        return CidadeDomain.builder()
                .id(cidade.getId())
                .nome(cidade.getNome())
                .codigo(cidade.getCodigo())
                .build();
    }

    public static Cidade gerarCidade2(){
        return Cidade.builder()
                .id(2L)
                .nome("São Paulo")
                .codigo("SAO_PAULO")
                .build();
    }

    public static CidadeDomain gerarCidadeDomain2(){
        Cidade cidade = gerarCidade2();
        return CidadeDomain.builder()
                .id(cidade.getId())
                .nome(cidade.getNome())
                .codigo(cidade.getCodigo())
                .build();
    }

    public static EventoRequestDTO criarEventoInsertDto(){
        CidadeIdRequestDTO cidadeIdRequestDTO = CidadeIdRequestDTO.builder().id(gerarCidade().getId()).build();
        return EventoRequestDTO.builder()
                .nome("evento teste")
                .data(LocalDate.now().plusDays(5L))
                .url("https://www.google.com.br/?hl=pt-BR")
                .cidade(cidadeIdRequestDTO)
                .build();
    }

    public static EventoDomain criarEventoDomainInsert(EventoRequestDTO eventoRequestDTO){
        CidadeDomain cidadeDomain = CidadeDomain.builder().id(eventoRequestDTO.getCidade().getId()).build();
        return EventoDomain.builder()
                .nome(eventoRequestDTO.getNome())
                .dataEvento(eventoRequestDTO.getData())
                .url(eventoRequestDTO.getUrl())
                .cidade(cidadeDomain)
                .build();
    }

    public static Evento criarEventoInsert(EventoDomain eventoDomain){
        Cidade cidade = Cidade.builder().id(eventoDomain.getCidade().getId()).build();
        return Evento.builder()
                .id(eventoDomain.getId())
                .nome(eventoDomain.getNome())
                .dataEvento(eventoDomain.getDataEvento())
                .url(eventoDomain.getUrl())
                .cidade(cidade)
                .createdAt(ZonedDateTime.now(UTC))
                .build();
    }

    public static EventoRequestDTO criarEventoUpdateDto(){
        CidadeIdRequestDTO cidadeIdRequestDTO = CidadeIdRequestDTO.builder().id(gerarCidade2().getId()).build();
        return EventoRequestDTO.builder()
                .nome("evento teste atualizado")
                .data(LocalDate.now().plusMonths(1L))
                .url("https://google.com.br")
                .cidade(cidadeIdRequestDTO)
                .build();
    }

    public static EventoDomain criarEventoDomainUpdate(Long id, EventoRequestDTO eventoRequestDTO){
        CidadeDomain cidadeDomain = CidadeDomain.builder().id(eventoRequestDTO.getCidade().getId()).build();
        return EventoDomain.builder()
                .id(id)
                .nome(eventoRequestDTO.getNome())
                .dataEvento(eventoRequestDTO.getData())
                .url(eventoRequestDTO.getUrl())
                .cidade(cidadeDomain)
                .build();
    }

    public static Evento criarEventoUpdate(EventoDomain eventoDomain){
        Cidade cidade = Cidade.builder().id(eventoDomain.getCidade().getId()).build();
        return Evento.builder()
                .id(eventoDomain.getId())
                .nome(eventoDomain.getNome())
                .dataEvento(eventoDomain.getDataEvento())
                .url(eventoDomain.getUrl())
                .cidade(cidade)
                .updatedAt(ZonedDateTime.now(UTC))
                .build();
    }

    public static EventoRequestDTO criarEventoInsertDtoVazio(){
        CidadeIdRequestDTO cidadeIdRequestDTO = CidadeIdRequestDTO.builder().id(null).build();
        return EventoRequestDTO.builder()
                .nome(null)
                .data(null)
                .url(null)
                .cidade(cidadeIdRequestDTO)
                .build();
    }
}
