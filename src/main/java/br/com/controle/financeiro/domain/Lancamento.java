package br.com.controle.financeiro.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "lancamentos")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;

    @JoinColumn(name = "id_conta")
    @ManyToOne
    private Conta conta;

    @JoinColumn(name = "id_categoria")
    @ManyToOne
    private Categoria categoria;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate data;

    @Column(precision = 8,scale = 2)
    private BigDecimal valor;

    @Column
    private boolean pago;
}
