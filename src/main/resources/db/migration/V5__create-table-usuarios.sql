--SPRINGSECURITY - 2 - criando a migration para criar a tabela usuarios no BD para dar continuidade na modificacao do springsecuroty Apos criar rodo o spring para o FLYWAY detectar a nova migration
create table usuarios(

    id bigint not null auto_increment,
    login varchar(100) not null,
    senha varchar(255) not null,

    primary key(id)

);