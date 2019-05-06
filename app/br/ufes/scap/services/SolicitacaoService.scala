package br.ufes.scap.services

import br.ufes.scap.persistence.Solicitacoes
import br.ufes.scap.models.{Solicitacao, SolicitacaoFull}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent._

object SolicitacaoService {

  def addSolicitacao(solicitacao: Solicitacao): Future[String] = {
    Solicitacoes.add(solicitacao)
  }

  def deleteSolicitacao(id: Long): Future[Int] = {
    Solicitacoes.delete(id)
  }

  def getSolicitacao(id: Long): Future[Option[Solicitacao]] = {
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
  
  def turnSolicitacaoIntoSolicitacaoFull
  (oldSolicitacao : Option[Solicitacao]): SolicitacaoFull = {
    val user = Await.result(UserService.getUser(oldSolicitacao.get.idProfessor),Duration.Inf)
    val solicitacao = SolicitacaoFull(oldSolicitacao.get.id, user,
              oldSolicitacao.get.idRelator, oldSolicitacao.get.dataSolicitacao, 
              oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
              oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
              oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
              oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
              oldSolicitacao.get.statusSolicitacao,oldSolicitacao.get.motivoCancelamento, 
              oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
  def addRelator(oldSolicitacao : Option[Solicitacao], idRelator : Long): Solicitacao = {
    val solicitacao = Solicitacao(oldSolicitacao.get.id, 
      oldSolicitacao.get.idProfessor,
      idRelator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      oldSolicitacao.get.statusSolicitacao,oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }
  
    def cancelaSolicitacao(oldSolicitacao : Option[Solicitacao]): Solicitacao = {
    val solicitacao = Solicitacao(oldSolicitacao.get.id, 
      oldSolicitacao.get.idProfessor,
      oldSolicitacao.get.idRelator, oldSolicitacao.get.dataSolicitacao, 
      oldSolicitacao.get.dataIniAfast, oldSolicitacao.get.dataFimAfast, 
      oldSolicitacao.get.dataIniEvento, oldSolicitacao.get.dataFimEvento, 
      oldSolicitacao.get.nomeEvento, oldSolicitacao.get.cidade, 
      oldSolicitacao.get.onus, oldSolicitacao.get.tipoAfastamento, 
      "CANCELADA",oldSolicitacao.get.motivoCancelamento, 
      oldSolicitacao.get.dataJulgamentoAfast)
    return solicitacao
  }

}
