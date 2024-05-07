package br.com.controle.financeiro.domain;

import br.com.controle.financeiro.domain.user.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "contas")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;

    @JoinColumn(name = "id_usuario")
    @ManyToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "conta")
    private List<Lancamento> lancamentos;

}
