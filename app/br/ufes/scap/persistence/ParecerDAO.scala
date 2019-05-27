package br.ufes.scap.persistence

import br.ufes.scap.models.{Parecer}

trait ParecerDAO extends BaseDAO {

  def findBySolicitacao(idSolicitacao : Long): Seq[Parecer]
  
  def findByProfessor(idProfessor : Long): Seq[Parecer]
  
}
