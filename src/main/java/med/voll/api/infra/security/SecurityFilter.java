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
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);
            var usuario = repository.findByLogin(subject);

            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);/*doFilter para continuar a requisicao e passo para ele o request e o response. Ele encaminha isso para o proximo filntro.

        FilterChain.doFilter(request, response): necessario para chamar os proximos filtros na aplicacao. PRECISO GARANTIR QUE ESSA LINHA SUPERIOR ESTEJA*/
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }

}
