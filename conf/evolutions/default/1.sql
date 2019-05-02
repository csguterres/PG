# User schema

# --- !Ups
create table `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `nome` TEXT NOT NULL,
  `matricula` TEXT NOT NULL,
  `email` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `tipo` TEXT NOT NULL
);

create table `solicitacao` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idProfessor` BIGINT NOT NULL,
  `idRelator` BIGINT NOT NULL,
  `dataSolicitacao` DATETIME NOT NULL,
  `dataIniAfast` DATETIME NOT NULL,
  `dataFimAfast` DATETIME NOT NULL,
  `dataIniEvento` DATETIME NOT NULL,
  `dataFimEvento` DATETIME NOT NULL,
  `nomeEvento` TEXT NOT NULL,
  `cidade` TEXT NOT NULL,
  `onus` TEXT NOT NULL,
  `tipoAfastamento` TEXT NOT NULL,
  `statusSolicitacao` TEXT NOT NULL,
  `motivoCancelamento` TEXT,
  `dataJulgamentoAfast` DATETIME,
	FOREIGN KEY (idProfessor)
        REFERENCES user(id)
		ON DELETE CASCADE
);

create table `parentesco` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idProfessor1` BIGINT NOT NULL,
  `idProfessor2` BIGINT NOT NULL,
	FOREIGN KEY (idProfessor1)
        REFERENCES user(id)
		ON DELETE CASCADE,
	FOREIGN KEY (idProfessor2)
        REFERENCES user(id)
		ON DELETE CASCADE
);

create table `mandato` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idProfessor` BIGINT NOT NULL,
  `cargo` TEXT NOT NULL,
  `dataIniMandato` DATETIME NOT NULL,
  `dataFimMandato` DATETIME NOT NULL,
	FOREIGN KEY (idProfessor)
        REFERENCES user(id)
		ON DELETE CASCADE
)

# --- !Downs
drop table `solicitacao`
drop table `mandato`
drop table `parentesco`
drop table `user`

