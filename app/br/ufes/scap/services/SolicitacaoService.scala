package br.ufes.scap.services

import br.ufes.scap.persistence.Solicitacoes
import br.ufes.scap.models.Solicitacao
import scala.concurrent.Future

object SolicitacaoService {

  def addUser(solicitacao: Solicitacao): Future[String] = {
    Solicitacoes.add(solicitacao)
  }

  def deleteUser(id: Long): Future[Int] = {
    Solicitacoes.delete(id)
  }

  def getUser(id: Long): Future[Option[Solicitacao]] = {
    Solicitacoes.get(id)
  }

  def listAllSolicitacoes: Future[Seq[Solicitacao]] = {
    Solicitacoes.listAll
  }
  
  def listAllSolicitacoesBySolicitante(idProfessor : Long): Future[Seq[Solicitacao]] = {
    Solicitacoes.findBySolicitante(idProfessor)
  }
  
  def update(solicitacao : Solicitacao): Future[String] = { 
    Solicitacoes.update(solicitacao)
  }
  

}
