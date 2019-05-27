package br.ufes.scap.persistence

import br.ufes.scap.models.{Solicitacao}

trait SolicitacaoDAO extends BaseDAO {

  def findById(id : Long): Seq[Solicitacao] 

  def findBySolicitante(idProfessor : Long): Seq[Solicitacao] 
  
  def findByRelator(idRelator : Long): Seq[Solicitacao] 

  def findByStatus(status : String): Seq[Solicitacao] 
  
}
