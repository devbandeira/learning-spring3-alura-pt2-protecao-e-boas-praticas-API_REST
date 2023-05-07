package med.voll.api.domain.usuario;
/*SPRINGSECURITY 3 - criando nossa interface repository que extende(HERDA) JPARepository do proprio Spring Data*/
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {/*Dentro dos generics <> passo quem e a entidade e o tipo da chave primaria*/
    UserDetails findByLogin(String login);/* SPRINGSECURITY 5 - troquei o nome de username para login. Esse e o metodo que vai fazer a consulta do usuario no banco de dados na table ade usuario. Esse metodo vai ser usado na nossa AutenticacaoService*/
}

/*Entao ate aqui no passo 5 temos: Entidade JPA que representa nosso usuario, temos as migrations (flyway) que criou nossa tabela no DB, temos a interface Repository que faz o acesso na tabela usuarios e temos a classe SERVICE que representa o servico de autenticacao que vai ser chamada automaticamente pelo SPRING quando tentarmos nos autenticar.*/