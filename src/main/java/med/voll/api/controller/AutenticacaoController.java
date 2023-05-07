package med.voll.api.controller;
/*SPRING SECURIRY - 7 ->*/
import jakarta.validation.Valid;
import med.voll.api.domain.usuario.DadosAutenticacao;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.infra.security.DadosTokenJWT;
import med.voll.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {
    /*no spring security nao chamoas a classe AutenticacaoService diretamente, temos uma outra calsse do spring
     * que vamos chamar e essa outra classe, chama o AutenticacaoService*/
    /*Preciso utilizar uma classe do proprio spring para, essa classe dispara o processo de autenticacao. ""AuthenticationManager"" nome dela.
     * */
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());/*esse token e o LOGIN e SENHA e ele ja esta representado no meu DTO DadosAutenticacao*//*Mas esse DTO n e um parametro que o SPRING espera, ele espera uma outra classe dele, nao uma classe do nosso projeto.
        Entao vamos criar uma classe que representa o usuario e a senha.*/ /*Criando a variavel authenticationToken : Digo "Converte do nosso DTO para esse UsernamePasswordAuthenticationToken que e como se fosse um DTO do proprio spring*/
        var authentication = manager.authenticate(authenticationToken);/*chamar o obj manager,authenticate(um objeto do tipo UsernamePasswordAuthenticationToken)*/

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        /*Se eu rodar o programa  return ResponseEntity.ok().build(); ele vai dar erro, pois ele nao vai encontrar o nosso ""AuthenticationManager manager"", vai dar erro no campo managem, na classe AutenticacaoController requer um BEAN do tipo AuthenticationManager que nao pode ser encontrado
        * entao, nao conseguiu injetar o atributo MANAGER na nossa classe controller.
        * Essa classe AuthenticationManager e do proprio spring, porem ele nao sabe injetar automaticamente um objeto AuthenticationManager. Temos que configurar.
        * La na classe SecurityConfigurations.  */
    }

}
