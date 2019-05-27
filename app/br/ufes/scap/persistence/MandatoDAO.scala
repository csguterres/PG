package br.ufes.scap.persistence

import br.ufes.scap.models.{Mandato}

trait MandatoDAO extends BaseDAO {
  
  def findByProfessor(idProfessor : Long): Seq[Mandato] 

}
