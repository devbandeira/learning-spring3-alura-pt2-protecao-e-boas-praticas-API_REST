package med.voll.api.domain.usuario;

/*esse dto SPRING SECURIRY - 7 -> representa apenas os dados que o usuarios ta enviando para efetuar login no Autenticacaocontroller*/
public record DadosAutenticacao(String login, String senha) {
}
