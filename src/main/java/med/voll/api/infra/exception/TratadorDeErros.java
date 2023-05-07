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

    /*Personalizando o ERRO 400, para nao retornar uma array dificil no retorno do erro padrao SPRING*/
    @ExceptionHandler(MethodArgumentNotValidException.class)/*Nome da exception que acontece pelo BEAN VALIDATION MethodArgumentNotValidException (que e o erro tratado nos parametros por nos @NotNull, @NotBlank, etc.*/
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        /*Diferente do erro 404, aqui eu preciso capturar a EXCEPTION que foi lancada, para saber quais campos que estao invalidos de acordo com o bean validation. ENTAO NA SSINATURA DO METODO EU POSSO RECEBER A EXCEPTION QUE FOI LANCADA.
        * MethodArgumentNotValidException ex ("botando a exceptiona na assitura do metodo para receber a EXCEPTION")*/
        var erros = ex.getFieldErrors();/*Jogando em uma variavel para PEGAR OS ERROS QUE ACONTECERAM, RETORNARA ISSO EM UMA LISTA DE ERROS EM CADA UM DOS CAMPOS*/
        /*PARA TESTE vou passar o erros para retornar direto e ver o que me retorna return ResponseEntity.badRequest().body(erros) nesse caso n preciso usar o .build() ja que passo o body. Mas vi que ele retorna aquele mesmo JSON que tem todas as informacoes que vim aqui tratar*/
        /*Entao para retornar somente o que eu quero da Exception lancad,a eu vou ter que criar um DTO. Como vou usar
        * esse DTO somente aqui dentro, vou criar ele aqui mesmo e vou chamar de DadosErroValidacao*/
        /*Entao no body eu nao vou passar uma lista de "erros", eu vou ter que converter esse lista para uma lista de DadosErroValidacao*/

        /*******************************************************************/
        /*Chamando os recursos do JAVA 8 para fazer a conversao de uma lista para outra "erros.stream().map()": erros me da uma stream e mapeia cada objetio FieldErros para um objeto DadosErroValidacao::new(::new) para chamar o construtor e .toList() para converser para uma lista */
        /*So dessa forma citada a cima dara erro, porque ainda nao tenho um construtor dentro do meu DTO um objeto Field.
        * Entao dentro do meu RECORD eu vou criar um construtor que recebe o OBJETO fieldError  */

        /*se eu deixar return ResponseEntity.badRequest().build() nao me retorna um erro na resposta, somente o codigo do erro 400 bad request. Faltou o corpo da resposta dizendo qual foi o dado invalido o que dificulta o usuario da nossa api*/
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
        /**/
    }

    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());/*erro.getFiel() me da o nome do campo e getDefaultMessage() me da a mensagem para aqueel campo especifico*/
        }
    }

}

/*Para nao poluir nosso controller, podemos usar outro recurso do spring e isolar esse tipo de tratamento de erros. A ideia e criar uma classe e la ter um metodo que vai ser responsavel por tratar esse tipo de erro e dentro do main.java no pacote principal temos (endereco, medico e paciente) esses sao do dominio da aplicacao entao vamos isolar eles em um pacote chamado DOMAIN. E crio um terceiro
 * pacote chamado INFRA e dentro dele crio a classe que vai tratar os erros TRATADORDEERROS. Aqui tera os tratamentos isolados. Ate entao e uma classe que nao tem nada a ver com o SPRING, ele nao vai carregar essa classe. Entao vou usar a anotacao para tratar error "@RestControllerAdvice" e dentro dela vou criar um metodo que vai lidar com aquela exception (ENPITYNOTFOUND EXCEPTION) tratarErro404(){} e anotar ele com
 * @ExceptionHandler(EntityNotFoundException.class) e dentro da anotacao passo como parametro o erro que vai ser tratado. ENTAO O SPRING JA SABE QUE EM QUALQUER DO NOSSO PROJETO FOR LANCADO UMA EXCEPTION ENTITYNOTFOUND ele vai chamar esse metodo TratarErro404(){} e o tipo de retorno (void n vai ser void)do metodo vai ser ResponseEntity que e o mesmo que usamos no controller que vai ter um return ResponseEntiry.notFound().build(); para ele criar o objeto ResponseEntity*/
