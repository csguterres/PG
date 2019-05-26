package br.ufes.scap.persistence

import br.ufes.scap.models.{Parentesco}
import scala.concurrent.Future


trait ParentescoDAO extends BaseDAO {

  def findByProfessor(idProfessor : Long): Future[Seq[Parentesco]] 
  
  def listAll: Future[Seq[Parentesco]] 


}
