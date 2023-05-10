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

    /*JWT 3 -> Vou injetar a classe TokenService e chamar o metodo para gerar o JWT. Tomar cuidado que o Spring
    * tem uma classe chamada TokenService, observar de importar a nossa criada pelo nosso projeto*/
    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());/*esse token e o LOGIN e SENHA e ele ja esta representado no meu DTO DadosAutenticacao*//*Mas esse DTO n e um parametro que o SPRING espera, ele espera uma outra classe dele, nao uma classe do nosso projeto.
        Entao vamos criar uma classe que representa o usuario e a senha.*/ /*Criando a variavel authenticationToken : Digo "Converte do nosso DTO para esse UsernamePasswordAuthenticationToken que e como se fosse um DTO do proprio spring*/
        var authentication = manager.authenticate(authenticationToken);/*chamar o obj manager,authenticate(um objeto do tipo UsernamePasswordAuthenticationToken)*/

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        /*JWT 3 -> Vou injetar a classe TokenService e chamar o metodo para gerar o JWT.*/
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        /*Se eu rodar o programa  return ResponseEntity.ok().build(); ele vai dar erro, pois ele nao vai encontrar o nosso ""AuthenticationManager manager"", vai dar erro no campo managem, na classe AutenticacaoController requer um BEAN do tipo AuthenticationManager que nao pode ser encontrado
        * entao, nao conseguiu injetar o atributo MANAGER na nossa classe controller.
        * Essa classe AuthenticationManager e do proprio spring, porem ele nao sabe injetar automaticamente um objeto AuthenticationManager. Temos que configurar.
        * La na classe SecurityConfigurations.  */
        /*se as credencias enviadas pelo front end quando consultada no BD existir, retornaremos o JWT, caso nao existe, retornaremos um 403 pois no processo de autenticacao o spring n achou o usuario enviado pela requisicao*/

        /*JWT 1: Quando o usuario for logar, aqui vai acontecer a requisicao do /LOGIN e preciso passar no return um TOKENJWT
        * crio entao o tokenJWT para passar no retur
        * Para acessar e por a lib para gerar JWT: jwt.io (site e pesquisa por libs para java)n*/
    }

}
