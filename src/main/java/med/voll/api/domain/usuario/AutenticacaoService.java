package med.voll.api.domain.usuario;
/*SPRINGSECURITY 4 - Essa classe vai ter a logica de autenticacao do projeto. O spring para conhecer essa classe, vai receber a anotacao @Service.
* To dizendo pro spring que essa classe e uma classe de servico para o spring.
* Mas o Spring precisa saber que essa claase e a classe que representa o servico de autenticacao. E eu preciso dizer
* isso para ele TAMBEM. DIGO ISSO PARA ELE IMPLEMENTANDO UMA INTERFACE ""UserDatailsService"", essa classe representa o servico de autenticacao.
*Nao vamos injetar em nenhum controler, o spring sozinho encontra essa classe e chama no meio da autenticacao. Desde que
* essa classe tenha a ANOTACAO @Service e IMPLEMENTE A UserDatailsSerivce.
* Toda vez que implemento uma interface, sou obrigado a IMPLEMENTAR OS METODOS DELA. FICARA DANDO ERRO, MAS VOU IMPLEMENTAR OS METODOS DELA
* ""PUBLIC UserDetails loadUserByUsername()""
* */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    /*posso usar o esquema de LOMBOK e gerar um contrutor com esse atributo, tanto faz @Autowired ou LOMBOK para fazer injecao de dependencia */
    private UsuarioRepository repository;


    /*O SPRING vai chamar esse meotod automaticamente no nosso sistema, toda vez que fizer LOGIN. Sempre que fizer login
    * o spring vai buscar a classe AutenticacaoService, pq ela IMPLEMENTA UserDatailsService e vai chamar o metodo loadUserByUsername(passando os dados que foram passados no front)*/
    /*Como aqui temos que acessar o banco de dados, vou fazer o Autowired, pois a minha funcao loadUserByUsername precisa retornar algo diferente de null.
    * entao vou INJETAR O REPOSITORY AQUI COMO UMA DEPENDENCIA para acesso ao nosso DB.*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);/*como n existe o findByLogin() vou criar na nossa INTERFACE USUARIOREPOSITORRY*/
    }
}
