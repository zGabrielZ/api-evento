package br.com.gabrielferreira.evento.service;

import br.com.gabrielferreira.evento.dao.QueryDslDAO;
import br.com.gabrielferreira.evento.domain.CidadeDomain;
import br.com.gabrielferreira.evento.domain.EventoDomain;
import br.com.gabrielferreira.evento.domain.UsuarioDomain;
import br.com.gabrielferreira.evento.entity.*;
import br.com.gabrielferreira.evento.repository.filter.EventoFilters;
import br.com.gabrielferreira.evento.repository.filter.UsuarioFilters;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.com.gabrielferreira.evento.utils.DataUtils.*;
import static br.com.gabrielferreira.evento.utils.PageUtils.*;

@Service
@RequiredArgsConstructor
public class ConsultaAvancadaService {

    private final QueryDslDAO queryDslDAO;

    public Page<EventoDomain> buscarEventos(EventoFilters filtros, Pageable pageable, Map<String, String> atributoDtoToEntity){
        pageable = validarOrderBy(pageable, atributoDtoToEntity);

        QEvento qEvento = QEvento.evento;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        validarFiltroEventos(filtros, booleanBuilder, qEvento);

        List<EventoDomain> eventoDomains = queryDslDAO.query(q -> q.select(Projections.constructor(
                        EventoDomain.class,
                        qEvento.id,
                        qEvento.nome,
                        qEvento.dataEvento,
                        qEvento.url,
                        Projections.constructor(
                                CidadeDomain.class,
                                qEvento.cidade.id,
                                qEvento.cidade.nome,
                                qEvento.cidade.codigo,
                                qEvento.createdAt,
                                qEvento.updatedAt
                        ),
                        qEvento.createdAt,
                        qEvento.updatedAt
                ))).from(qEvento)
                .innerJoin(qEvento.cidade)
                .where(booleanBuilder)
                .orderBy(getOrder(pageable.getSort(), Evento.class))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        eventoDomains.forEach(eventoDomain -> {
            eventoDomain.setCreatedAt(toFusoPadraoSistema(eventoDomain.getCreatedAt()));
            eventoDomain.setUpdatedAt(toFusoPadraoSistema(eventoDomain.getUpdatedAt()));
            eventoDomain.getCidade().setCreatedAt(toFusoPadraoSistema(eventoDomain.getCidade().getCreatedAt()));
            eventoDomain.getCidade().setUpdatedAt(toFusoPadraoSistema(eventoDomain.getCidade().getUpdatedAt()));
        });

        return new PageImpl<>(eventoDomains, pageable, eventoDomains.size());
    }

    public Page<UsuarioDomain> buscarUsuarios(UsuarioFilters filtros, Pageable pageable, Map<String, String> atributoDtoToEntity){
        pageable = validarOrderBy(pageable, atributoDtoToEntity);

        QUsuario qUsuario = QUsuario.usuario;
        QPerfil qPerfil = QPerfil.perfil;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        validarFiltroUsuario(filtros, booleanBuilder, qUsuario, qPerfil);

        List<UsuarioDomain> usuarios = queryDslDAO.query(q -> q.select(Projections.constructor(
                        UsuarioDomain.class,
                        qUsuario.id,
                        qUsuario.nome,
                        qUsuario.email,
                        qUsuario.createdAt,
                        qUsuario.updatedAt
                ))).from(qUsuario)
                .distinct()
                .innerJoin(qUsuario.perfis, qPerfil)
                .where(booleanBuilder)
                .orderBy(getOrder(pageable.getSort(), Usuario.class))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        usuarios.forEach(usuarioDomain -> {
            usuarioDomain.setCreatedAt(toFusoPadraoSistema(usuarioDomain.getCreatedAt()));
            usuarioDomain.setUpdatedAt(toFusoPadraoSistema(usuarioDomain.getCreatedAt()));
        });

        return new PageImpl<>(usuarios, pageable, usuarios.size());
    }

    private void validarFiltroEventos(EventoFilters filtros, BooleanBuilder booleanBuilder, QEvento qEvento){
        if(filtros.isIdExistente()){
            booleanBuilder.and(qEvento.id.eq(filtros.getId()));
        }

        if(filtros.isNomeExistente()){
            booleanBuilder.and(qEvento.nome.likeIgnoreCase(Expressions.asString("%").concat(filtros.getNome().trim()).concat("%")));
        }

        if(filtros.isDataExistente()){
            booleanBuilder.and(qEvento.dataEvento.goe(filtros.getData()));
        }

        if(filtros.isUrlExistente()){
            booleanBuilder.and(qEvento.url.eq(filtros.getUrl()));
        }

        if(filtros.isIdCidadeExistente()){
            booleanBuilder.and(qEvento.cidade.id.eq(filtros.getIdCidade()));
        }

        if(filtros.isCreatedAtExistente()){
            ZonedDateTime createdAt = filtros.getCreatedAt().atStartOfDay(UTC);
            booleanBuilder.and(qEvento.createdAt.goe(createdAt));
        }

        if(filtros.isUpdatedAtExistente()){
            ZonedDateTime updatedAt = filtros.getUpdatedAt().atStartOfDay(UTC);
            booleanBuilder.and(qEvento.updatedAt.goe(updatedAt));
        }
    }

    private void validarFiltroUsuario(UsuarioFilters filtros, BooleanBuilder booleanBuilder, QUsuario qUsuario, QPerfil qPerfil){
        if(filtros.isIdExistente()){
            booleanBuilder.and(qUsuario.id.eq(filtros.getId()));
        }

        if(filtros.isNomeExistente()){
            booleanBuilder.and(qUsuario.nome.likeIgnoreCase(Expressions.asString("%").concat(filtros.getNome().trim()).concat("%")));
        }

        if(filtros.isEmailExistente()){
            booleanBuilder.and(qUsuario.email.eq(filtros.getEmail()));
        }

        if(filtros.isIdsPerfisExistente()){
            booleanBuilder.and(qPerfil.id.in(filtros.getIdsPerfis()));
        }

        if(filtros.isCreatedAtExistente()){
            ZonedDateTime createdAt = filtros.getCreatedAt().atStartOfDay(UTC);
            booleanBuilder.and(qUsuario.createdAt.goe(createdAt));
        }

        if(filtros.isUpdatedAtExistente()){
            ZonedDateTime updatedAt = filtros.getUpdatedAt().atStartOfDay(UTC);
            booleanBuilder.and(qUsuario.updatedAt.goe(updatedAt));
        }
    }

    private OrderSpecifier<?>[] getOrder(Sort sorts, Class<?> classe){
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        PathBuilder<?> entityPath = new PathBuilder<>(classe, classe.getSimpleName().toLowerCase());
        for (Sort.Order sort : sorts) {
            String propriedade = sort.getProperty();
            String direcao = sort.getDirection().name();

            OrderSpecifier<?> orderSpecifier = "asc".equalsIgnoreCase(direcao)
                    ? entityPath.getString(propriedade).asc()
                    : entityPath.getString(propriedade).desc();

            orderSpecifiers.add(orderSpecifier);
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
