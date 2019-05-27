package br.ufes.scap.persistence

import br.ufes.scap.models.{Parentesco}


trait ParentescoDAO extends BaseDAO {

  def findByProfessor(idProfessor : Long): Seq[Parentesco] 
  

}
