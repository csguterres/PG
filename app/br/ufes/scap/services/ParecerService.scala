package br.ufes.scap.services

import br.ufes.scap.persistence.Pareceres
import br.ufes.scap.models.Parecer
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date

object ParecerService {

  def addParecer(parecer: Parecer): Future[String] = {
    Pareceres.add(parecer)
  }

  def deleteParecer(id: Long): Future[Int] = {
    Pareceres.delete(id)
  }

  def getParecer(id: Long): Future[Option[Parecer]] = {
    Pareceres.get(id)
  }

  def listAllPareceres: Future[Seq[Parecer]] = {
    Pareceres.listAll
  }
  
  def listAllPareceresBySolicitacao(idSolicitacao : Long): Future[Seq[Parecer]] = {
    Pareceres.findBySolicitacao(idSolicitacao)
  }
    
  def update(parecer : Parecer): Future[String] = { 
    Pareceres.update(parecer)
  }
  
}
