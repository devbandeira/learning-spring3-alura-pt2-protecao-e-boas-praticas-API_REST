package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*CONTROLE DE ACESSO 1: Criando a classe de filtro*/
@Component/*CONTROLE DE ACESSO 2: como essa classe nao e um controler, nao e ClasseService(regra de negocio, validacao, regra de servico), nao e um repository, nao e uma classe de configuracao(nao uma classe onde vou configurar coisas para o spring no projeto)
Nesses cenarios onde queremos que o SPRING carregue uma classe, mas ela nao tem um tipo especifico. @Component e utilzado para que o spring carregue uma classe/componente generico*/
public class SecurityFilter extends OncePerRequestFilter {/*ao inves de usar Filter do servelet, vou usar do spring para carregar tudo*/
   /*OncePerRequestFilter garante que vai ocorrer somente uma vez para cada requisicao*/
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    /*metodo que o spring vai chamar quando esse filtro for executado. Esse metodo e criado devido a HERANCA*/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {/*Esse metodo implementado recebe 3 parametros: request (pegamos coisas da requisicao), response(enviar coisas na resposta), filterChain(representa a cadeira de filtros que existe na aplicacao. Uso o filterChain para chamar o proximo filtro, pois se eu nao chamar o proximo passo/filtro nao me retorna nada, mas execita*/
        var tokenJWT = recuperarToken(request);/*para recuperar o TOKEN. Criei um metodo recuperarTOKEN() ele cria dentro da propria classe SecurityFilter */
        /*System.out.println(tokenJWT); somente para ver se dava pra recuperar algo do cabecalho token Authorization qnd receber requisicoes*/
        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var usuario = repository.findByLogin(subject);

            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);/*doFilter para continuar a requisicao e passo para ele o request e o response. Ele encaminha isso para o proximo filntro.

        FilterChain.doFilter(request, response): necessario para chamar os proximos filtros na aplicacao. PRECISO GARANTIR QUE ESSA LINHA SUPERIOR ESTEJA*/
    }

    private String recuperarToken(HttpServletRequest request) {/*esse metodo eu passo para ele o request e ele vai me retornar a string do token*/
        var authorizationHeader = request.getHeader("Authorization");/*para pegar/recuperar o cabecalho eu vou jgoar isso em uma variavel. O metodo getHeader(Recebe uma string com o nome do cabecalho). Se n existir o cabecalho, vai me devolver um null*/
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");/*PEga o token que vem do cabecalho e substitui o bearer(tipo de token) que vem do cabecalho como prefixo e substitui pro ""*/
        }

        return null;
    }

}
