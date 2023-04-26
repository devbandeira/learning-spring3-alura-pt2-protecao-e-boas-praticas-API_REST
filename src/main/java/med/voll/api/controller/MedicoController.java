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
        var medico = new Medico(dados);
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
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
* adicionar o retorno INSTANCIANDO O OBJETO RESPONSEENTITY . Utilizando seus metodos estaticos.*/
        /*Se eu por somente ate o noContent(). ele da um erro de compilacao porque o noContent() nao retorna um objeto RESPONSENEITY, entao uso o BUILD.
        * noContent() cria o objeto e chamo o BUILD para ai sim construir o objeto.*/
        return ResponseEntity.noContent().build();/*Respondo "Codigo 204" dizendo que a requisicao foi processada e sem um corpo/conteudo*/
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }


}
