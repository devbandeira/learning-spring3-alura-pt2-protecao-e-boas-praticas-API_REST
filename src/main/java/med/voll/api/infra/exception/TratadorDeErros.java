package med.voll.api.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }//.notFound().build() e para criar o erro 404 "notFound() e retornar ele 404.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}

/*Para nao poluir nosso controller, podemos usar outro recurso do spring e isolar esse tipo de tratamento de erros. A ideia e criar uma classe e la ter um metodo que vai ser responsavel por tratar esse tipo de erro e dentro do main.java no pacote principal temos (endereco, medico e paciente) esses sao do dominio da aplicacao entao vamos isolar eles em um pacote chamado DOMAIN. E crio um terceiro
 * pacote chamado INFRA e dentro dele crio a classe que vai tratar os erros TRATADORDEERROS. Aqui tera os tratamentos isolados. Ate entao e uma classe que nao tem nada a ver com o SPRING, ele nao vai carregar essa classe. Entao vou usar a anotacao para tratar error "@RestControllerAdvice" e dentro dela vou criar um metodo que vai lidar com aquela exception (ENPITYNOTFOUND EXCEPTION) tratarErro404(){} e anotar ele com
 * @ExceptionHandler(EntityNotFoundException.class) e dentro da anotacao passo como parametro o erro que vai ser tratado. ENTAO O SPRING JA SABE QUE EM QUALQUER DO NOSSO PROJETO FOR LANCADO UMA EXCEPTION ENTITYNOTFOUND ele vai chamar esse metodo TratarErro404(){} e o tipo de retorno (void n vai ser void)do metodo vai ser ResponseEntity que e o mesmo que usamos no controller que vai ter um return ResponseEntiry.notFound().build(); para ele criar o objeto ResponseEntity*/
