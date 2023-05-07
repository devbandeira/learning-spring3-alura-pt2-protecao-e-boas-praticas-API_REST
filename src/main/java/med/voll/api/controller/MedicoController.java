package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.medico.DadosListagemMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {
        /*O cadastro vai ser um pouco diferente pq existe um codigo 201 "Requisicao processada e novo recurso criado
         * . E esse codigo tem um tratamento especial. E vamos utilizar aqui tbm o que foi criado par ao atualizar
         * o DTO "DadosDetalhamentoMedico"
         * */

        /*De acordo com o PROTOCOLO HTTP: Qnd estamos postando uma informacao em uma api, o codigo HTTP que deve ser devolvido e o 201"Created"
         * Mas esse codigo tem algumas regras, na RESPOSTA temos que devolver o CODIGO 201 e devolver no CORPO DA RESPOSTA os dados do NOVO RECURSO/REGISTRO criado.
         * E devolver tambem um cabecalho do protocolo HTTP chamado location. Cabecalho que mostra o endereco para que o FRONT consigo acessar esse recurso que acabou de ser cadastrado.
         * */


        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        /*Comando CREATED(URI) que tenho que passar para ele uma URI que representa o endereco e o SPRING VAI CRIAR UM "CABECALHO LOCATION" automaticamente baseado na uri que passar como parametro.
         * Na sequencia .body() para eu passar o body com a informacao que eu quero devolver no corpo da resposta e ai ele cria o objeto RESPONSENTITY e no body passo nosso parametro DTO.
         * Crio a VAR URI -> A uri tem que ser o endereco da nossa API nesse caso http://localhost../medicos/idDoMEdicoQueAcabouDeSerCriadoNoBD. Entao para encapsular
         * para esconder o negocio da URI e nao ter que ficar controlando quando mudar a URI de local para producao. O SPRING ja tem uma classe que encapsula uma classe da API e ela ja faz a cosntrucao da URI automatica.
         * Para usar essa CLASSE NO METODO CADASTRAR eu vou por mais um parametro "UriComponentsBuilder uriBuilder". Basta receber esse parametro no metodo CONTROLER que o SPRING vai passar a URI sozinho.
         * ...Logo "var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();"
         * -> path() para passar o complemento da URL porque ele so vai criar a URI"localhost:8080" e ai vou colocar o complemento "/medicos/{id}" e na sequencia eu tenho que substituir esse ID pelo ID do medico que acabou de ser criado no BD.
         * .buildAndExpand() e passo o ID do medico que acabou de ser criado no BD. A o ID ta no repository.save() que foi onde mandei ele salvar no BD. Como passei anteriormente dentro do save (new Medico(dados)); Vou desacoplar.
         * jogando para uma variavel.
         * E passo no .buildAndExpand(medico.getID()) que o ID vai ser gerado na linha anterior pelo BD automaticamente.
         * E Na sequencia chamo o .toUri(); que ele gera a URI.*/
        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
        /* Entao passo isso tudo para o nosso return*/

        /*Percebo que tem esse role no metodo CADASTRAR: Devolver codigo 201, cabecalho location com a URI e devolver no corpo da resposta uma representacao do recurso recem criado*/
        /*Percebo tambem a ordem em que foi feita para quando chegar na hora de gerar a var URI -> Meu medico ja foi salva anteriormente no BD atravaes do repository.save(medico)*/


        /*Se eu olhar o Header que retornou na requisicao ele vai retornar o LACATION: http://localhost:8080/medicos/6, mas se eu entrar nessa URL nao vai retornar nada Codigo 404.
         *Pq n criamos uma requisicao para detalhar os DADOS MEDICOS.
         * */
    }

    @GetMapping
    /*Envolto o page<DadosListagemMedico> passando como GENERiCS e retornando um RESPONSEENTITY*/
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        /*troco o retorno porque ele nao vai retornar diretamente a variavel PAGE*/
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        return ResponseEntity.ok(page);/*Devolvendo o codigo 200 passando o codigo PAGE(paginacao) com os dados dos medicos*/
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
        /*devolvendo os dados medicos que foram atualizados*/
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));/*Dentro dos () tenho que passar o objeto MEDICO, mas nao posso passar ele direto.
        PQ esse objeto medico e uma ENTIDADE JPA e como premissa "NAO E RECOMENDADO DEVOLVER E RECEBER ENTEIDADES JPA NO CONTROLLER

        ENTAO VOU DEVOLVER UM DTO, MAS O DTO QUE TENHO "DADOSATUALIZACAOmeDICO" ele e incompleto, ele representa os dados da atualizacao do medico, o que vai ser enviado para att aas informacoes.
        Como quero devolver todos os dados atualizados do MEDICO, vou criar um outro DTO que quero devolver os dados "DadosDetalhamentoMedico".

        */
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
        /*Para fazer o retorno adequado tiro o VOID eu utilizo uma CLASSE do SPRING "RESPONSEENTITY" para retornar um OBJETO do tipo RESPONSEENTITY e
         *       adicionar o retorno INSTANCIANDO O OBJETO RESPONSEENTITY . Utilizando seus metodos estaticos.*/
        /*Se eu por somente ate o noContent(). ele da um erro de compilacao porque o noContent() nao retorna um objeto RESPONSENEITY, entao uso o BUILD.
         * noContent() cria o objeto e chamo o BUILD para ai sim construir o objeto.*/
        return ResponseEntity.noContent().build();/*Respondo "Codigo 204" dizendo que a requisicao foi processada e sem um corpo/conteudo*/
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        /*A forma tradicional de fazer esse erro retornar o erro correto 404 e usar o trycatch, mas fazer isso eu estaria apenas tratando o erro dentro desse metodo so que essa mesma exception pode acontecer em outros metodos alem do CONTROLER aqui de MEDICOS, ao inves de criar em cada controler poluindo.. vou enta*/
        /*Para nao poluir nosso controller, podemos usar outro recurso do spring e isolar esse tipo de tratamento de erros. A ideia e criar uma classe e la ter um metodo que vai ser responsavel por tratar esse tipo de erro e dentro do main.java no pacote principal temos (endereco, medico e paciente) esses sao do dominio da aplicacao entao vamos isolar eles em um pacote chamado DOMAIN. E crio um terceiro
         * pacote chamado INFRA e dentro dele crio a classe que vai tratar os erros TRATADORDEERROS. Aqui tera os tratamentos isolados. Ate entao e uma classe que nao tem nada a ver com o SPRING, ele nao vai carregar essa classe. Entao vou usar a anotacao para tratar error "@RestControllerAdvice" e dentro dela vou criar um metodo que vai lidar com aquela exception (ENPITYNOTFOUND EXCEPTION) tratarErro404(){} e anotar ele com
         * @ExceptionHandler(EntityNotFoundException.class) e dentro da anotacao passo como parametro o erro que vai ser tratado. ENTAO O SPRING JA SABE QUE EM QUALQUER DO NOSSO PROJETO FOR LANCADO UMA EXCEPTION ENTITYNOTFOUND ele vai chamar esse metodo TratarErro404(){} e o tipo de retorno (void n vai ser void)do metodo vai ser ResponseEntity que e o mesmo que usamos no controller que vai ter um return ResponseEntiry.notFound().build(); para ele criar o objeto ResponseEntity*/
        var medico = repository.getReferenceById(id);/*Aqui esta o problema, se eu passar no metodo um ID que nao existe. Ele lanca uma exception ENPITY NOT FOUND. Como n fizemos o tratamento com um trycatcg, a exception aconteceu nessa linha, foi lancada pro spring e o spring por padrao, excecoes nao tratadas no codigo sao interpretadas pelo spring boot como erro 500 como resposta pro cliente*/
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }


}
