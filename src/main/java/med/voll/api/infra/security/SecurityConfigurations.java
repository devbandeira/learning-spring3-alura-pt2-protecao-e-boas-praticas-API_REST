package med.voll.api.infra.security;


/*SPRING SECURIRY - 6 : Aqui nessa classe vamos concentrar as configuracoes de seguranca*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration /*Como e uma classe de configuracoes, boto essa anotacao para o spring*/
@EnableWebSecurity /*Configurar de seguranca, personalizar o o processo de autenticacao e autorizacao anotacao do spring security*/
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    /*A primeira configuracao que queremos fazer aqui e para ela deixar de ser statefull para ser stateless*/
    /*Entao vou declarar um metodo que seu retorno vai ser um proprio objeto do Spring ""SecurityFilterChain"" (usamos para configurar coisas relacionadas a processo de autenticacao e tbm autorizacao.
    * Mos nao vamos instanciar o objeto SecurityFilterChain, nos parametros vou receber outra classe do spring ""HttpSecurity"".
    * Dentro do metodo eu boto um erturn que o meu parametro retun ""http"" e o proprio spring que fornece esse parametro para gente e ele tem alguns metodos para fazer algumas configuracoes para nos
    * e no final ele tem um metodo build() que cria o objeto ""SecurityFilterChain"".
    * -- csrf().disable() -> Desabilita ataques do tipo Cross-Site Request Forgert CSRF. Como vamos trabalhar com tokens o proprio token ja e uma protecao contra o CSRF.
    * -- .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) -> Configurando para ser Stateless. sessionManagement(). sistema de geranciacao de sessoo. sessionCreationPolicy() politica de criacao de sessao. Passa como parametro o objeto estatico STATELESS. STALESS PQ E UMA API REST
    * -- .build() -> Configurado ate aqui, ele nao vai ter mais o mesmo comportamento e ele nao vai fornecer a tela de login e bloquear todas as requisicoes isso vai ficar a cargo da nossa aplicacao frontend.
    * --
    * --
    * */
    @Bean/* Anotacao para exportar o retorno desse metodo para o SPRING. Devolver um objeto para o spring*/
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /*Criando este metodo para o spring entender como ele pode injetar o objeto, por tambem anotacao BEAN. Ele vai injetar no AuthenticacaoController*/
    @Bean/*@Bean serve para exportar uma classe para o Spring, fazendo com que ele consiga carrega-la e realize a sua injecao de dependencia em outras classes.*/
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();/*esse metodo tem essa classe(getAuthenticationManager) que sabe criar o objeto AuthenticationManager
        */
    }


    /*SPRING SECURIRY - 8 ou 7: PasswordEncoder classe que representa o algoritmo em hash de senha*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*BCryptPasswordEncoder classe do proprio string que conseguimos instanciar igual classe java*/


}
