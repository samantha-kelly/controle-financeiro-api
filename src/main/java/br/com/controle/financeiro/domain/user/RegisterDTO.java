package br.com.controle.financeiro.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
