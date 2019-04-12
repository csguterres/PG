# User schema

# --- !Ups
create table `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `nome` TEXT NOT NULL,
  `matricula` TEXT NOT NULL,
  `email` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `tipo` TEXT NOT NULL
)

# --- !Downs
drop table `user`
