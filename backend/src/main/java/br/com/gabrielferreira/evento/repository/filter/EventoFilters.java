package br.com.gabrielferreira.evento.repository.filter;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoFilters implements Serializable {

    @Serial
    private static final long serialVersionUID = -3550554456478798801L;

    private Long id;

    private String nome;

    private LocalDate data;

    private String url;

    private Long idCidade;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public boolean isIdExistente(){
        return this.id != null;
    }

    public boolean isNomeExistente(){
        return StringUtils.isNotBlank(this.nome);
    }

    public boolean isDataExistente(){
        return this.data != null;
    }

    public boolean isUrlExistente(){
        return StringUtils.isNotBlank(this.url);
    }

    public boolean isIdCidadeExistente(){
        return this.idCidade != null;
    }

    public boolean isCreatedAtExistente(){
        return this.createdAt != null;
    }

    public boolean isUpdatedAtExistente(){
        return this.updatedAt != null;
    }
}
