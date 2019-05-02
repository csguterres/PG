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
}
