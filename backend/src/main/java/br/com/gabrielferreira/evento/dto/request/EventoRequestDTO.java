package br.com.gabrielferreira.evento.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 958714108846456044L;

    @NotBlank(message = "Nome do evento não pode ser vazio")
    @Size(max = 150, message = "O nome do evento deve ter no máximo 150 caracteres")
    private String nome;

    @FutureOrPresent(message = "Data do evento deve ser no presente ou futuro")
    @NotNull(message = "Data do evento não pode ser vazio")
    private LocalDate data;

    @URL(message = "URL inválida")
    @Size(max = 250, message = "A url do evento deve ter no máximo 250 caracteres")
    private String url;

    @Valid
    @NotNull(message = "A cidade não pode ser vazia")
    private CidadeIdRequestDTO cidade;
}
