package med.voll.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/*SPRINGSECURITY 1 - E uma Entidade JPA*/
/*Cada entidade JPA vai ter um REPOSITORY que e a INTERFACE QUE FAZ O ACESSO AO BANCO DE DADOS DAQUELA TABELA daquela entidade em especifico*/
@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
/*SPRINGSECURITY - Automaticamente ela vai estar representando uma tabela no nosso banco de dados e entao preciso ter uma tabela no meu BD entao vou usar as migrations para criar a tabela no DB*/
public class Usuario implements UserDetails {
/*configurando: implementando uma interface para o Spring Security (UserDetails). Para o spring reconhecer nossa classe Usuario
* do nosso projeto. Ele precisa saber que o atributo login e o campo Login, etc.
* Fizeram um jeito dele entender isso. E a classe que representa o usuario e a classe ""UserDetails"", que e uma interface do proprio spring security e preciso implementar os metodos
* E mudar tudo que tem return false para TRUE, boto tudo true. Se eu quiser controlar se o usuario tem data de expiracao, se o usuario da bloqueado, etc.
* eu crio atributos e retorno os atributos especificos que representam essas informacoes. Mas aqui vou deixar tudo true
* Temos tambem o getPassword e GetUsername que return para eles o nome dos atributos de cada um.
* GetAuthorities -> Se eu quiser dar autorizacoes, se eu tiver perfil adm, perfil moderador, etc....Preciso cirar uma classe
* que represente esses perfis, nao vamos usar aqui, mas o spring nos obriga a retornar uma colecao de perfil.
* Entao vamos simular uma: list.of(new SimpleGrantedAuthority("ROLE_USER")) (fim da aula 29(controller de autenticacao) config basico do spring security)*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String senha;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
