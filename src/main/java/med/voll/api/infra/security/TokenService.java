package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
/*JWT 2 -> vai ser uma classe de servico, relacionada com token. Vai gerar, validar, etc, sobre token*/
@Service/*Como representa um servico, anoto com @Service*/
public class TokenService {

    @Value("${api.security.token.secret}")/*Dizendo para o spring boot ler um atributo de uma classe, para ler do aplication.properties. CUIDADO tem 2 VALUES para importar LOMBOK e SPRING, USAR DO SPRING!!!!*/
    private String secret;/*JWT 4 - Como isso e uma configuracao da nossa aplicacao, podemos por no aplication.properties. E eu consigo ler uma propriedade declarada dentro do aplication.properties em uma classe java*/

    /*Metodo para gerar TOKEN que retorna uma String pq o TOKEN e uma string.
    * Dentro do metodo vamos utilizar a biblioteca que add no projeto.
    * Pego o TRY/CATCH no proprio GITHUB do projeto auth0.*/
    public String gerarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);/*Aqui ele recebe uma string, string d senha secreta que e da nossa api. Que e a senha utilizadaa para fazer a assinatura do TOKEN*/
            return JWT.create()
                    .withIssuer("API Voll.med")/*diz qual e a ferrameta esta sendo utilizada para gerar esse TOKEN. veio como "oauth0" mas mudei para minha api que estara gerando esse token*/
                    .withSubject(usuario.getLogin())/*Armazenando a informacao a qual usuario esse token pertence, essas informacoes colocamos dentro do token, pq nossa api e stateless. Do lado do servidor, do lado na nossa API nao tem uma sessao com dados do usuario. Esse token e enviado pelo cliente e dentro desse token tem que ter alguma forma de identificar qual dos usuarios esta disparando a requisicao*/ /*Como eu to fazendo usuario.getLogin(), eu preciso passar a entidade usuario como parametro do metodo gerarToken(Usuario usuario)*/
                    /*se eu quiser mais informacoes posso usar o .withClaim("id", usuario.getId(), ou qualquer informacao que eu quiser guardar dentro do token com esse withClaim()) Como se fosse arquivo chave e valor chave, valor...*/
                    .withExpiresAt(dataExpiracao())/*Recomendado que tenha data de validade o TOKEN. Crio um metodo para criar nosso tempo de expiracao, que retorna um localDate*/
                    .sign(algoritmo);/*metodo para fazer a assinatura passando ao algoritmo*/
        } catch (JWTCreationException exception){
            throw new RuntimeException("erro ao gerar token jwt", exception);/*para nao ficar forcando o controller ou quem chamou isso aqui ficar fazendo TRY/CATCH eu lanco uma runtime*/
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Voll.med")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    private Instant dataExpiracao() {/*vou retornar um Instant que e do JAva 8, API DE DATAS*/
        /*pego a data atual e somo duas horas nesse caso. Na verdade vou retornar um toInstant*/
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));/*.toInstante para converter para um Instant. Preciso passar um objeto ZoneOffset para passar o TIMEZONE o fuso horario -03:00 para deixar a hora certo do brasil*/
    }
/*JWT 3 -> Para usar essa classe para gerar o token de fato. Uso ela la no meu AutenticacaoController, passando ela para a minha requisicao de login no PostMaping*/
}
