package br.ufes.scap.services

import br.ufes.scap.persistence.Parentescos
import br.ufes.scap.models.Parentesco
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date

object ParentescoService {

  def addParentesco(parentesco: Parentesco): Future[String] = {
    Parentescos.add(parentesco)
  }

  def deleteParentesco(id: Long): Future[Int] = {
    Parentescos.delete(id)
  }

  def getParentesco(id: Long): Future[Option[Parentesco]] = {
    Parentescos.get(id)
  }

  def listAllParentescos: Future[Seq[Parentesco]] = {
    Parentescos.listAll
  }
  
  def listAllParentescosByProfessor(idProfessor : Long): Future[Seq[Parentesco]] = {
    Parentescos.findByProfessor(idProfessor)
  }
    
  def update(parentesco : Parentesco): Future[String] = { 
    Parentescos.update(parentesco)
  }
  
  def checaDiferente(id1 : Long, id2 : Long):Boolean = {
       return id1 != id2
  }
          
  def naoExisteParentesco(id1 : Long, id2 : Long):Boolean = {
          val parentescos = Await.result(ParentescoService.listAllParentescosByProfessor(id1), Duration.Inf)
          for (p <- parentescos){
            if (p.idProfessor1 == id2 || p.idProfessor2 == id2){
              return false
            }
          }
          return true
        }
}
