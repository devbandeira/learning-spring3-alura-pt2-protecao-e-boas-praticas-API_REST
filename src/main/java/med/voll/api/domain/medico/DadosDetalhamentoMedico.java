package med.voll.api.domain.medico;

import med.voll.api.domain.endereco.Endereco;

/*O recordo vai ser CRIADO automaticamente recebendo um OBJETO MEDICO, mas nao quero isso. Entao vou passar as informacoes do medico "long id ......."*/

public record DadosDetalhamentoMedico(Long id, String nome, String email, String crm, String telefone, Especialidade especialidade, Endereco endereco) {

    /*Como la no MedicoCOntroller no metodo ATUALIZAR eu to passando um objeto MEDICO, eu posso criar um CONSTRUTOR AQUI
    * O construtor aqui recebe um OBJETO do tipo medico e ele chama o construtor principal "THIS" do nosso RECORD passando os parametros.
    * */
    public DadosDetalhamentoMedico(Medico medico) {
        this(medico.getId(), medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getTelefone(), medico.getEspecialidade(), medico.getEndereco());
    //o THIS aqui diz que to chamando o proprio contrutor do meu RECORD. CTRL + CLICK ele leva ate ele.
    }
}
