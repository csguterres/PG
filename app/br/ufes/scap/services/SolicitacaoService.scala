package br.ufes.scap.services

import br.ufes.scap.persistence.SolicitacaoDAOSlick
import br.ufes.scap.models.{Solicitacao, SolicitacaoFull, User, StatusSolicitacao}
import util.control.Breaks._

object SolicitacaoService {

  def addSolicitacao(solicitacao: Solicitacao) = {
    SolicitacaoDAOSlick.save(solicitacao)
  }

  def deleteSolicitacao(id: Long) = {
    SolicitacaoDAOSlick.delete(id)
  }

  def getSolicitacao(id: Long): SolicitacaoFull = {
    this.turnSolicitacaoIntoSolicitacaoFull(SolicitacaoDAOSlick.get(id).get)
  }

  def listAllSolicitacoes: Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.listAll
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def turnSeqSolIntoSeqSolFull(solicitacoes : Seq[Solicitacao]): Seq[SolicitacaoFull] = {
    var solicitacoesFull : Seq[SolicitacaoFull] = Seq()
    for (s <- solicitacoes){
      solicitacoesFull = solicitacoesFull :+ this.turnSolicitacaoIntoSolicitacaoFull(s)
    }
    return solicitacoesFull
  }
  
  def listAllSolicitacoesById(id : Long): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findById(id)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def listAllSolicitacoesBySolicitante(idProfessor : Long): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findBySolicitante(idProfessor)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def listAllSolicitacoesByRelator(idRelator : Long): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findByRelator(idRelator)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def listAllSolicitacoesByStatus(status : String): Seq[SolicitacaoFull] = {
    val solicitacoes = SolicitacaoDAOSlick.findByStatus(status)
    return turnSeqSolIntoSeqSolFull(solicitacoes) 
  }
  
  def update(solicitacao : SolicitacaoFull) = { 
    val sol = this.turnSolicitacaoFullIntoSolicitacao(solicitacao)
    SolicitacaoDAOSlick.update(sol)
  }
  
  def mergeListas(listSolicitacao1 : Seq[SolicitacaoFull], listSolicitacao2 : Seq[SolicitacaoFull]) : Seq[SolicitacaoFull] = {
      var solicitacoes : Seq[SolicitacaoFull] = Seq()
      for (s <- listSolicitacao2){
          var entra = false
          for(sol <- listSolicitacao1){
              if (s.id == sol.id){
                entra = true ;
                break
              }
          }
          if(entra == true){
            solicitacoes = solicitacoes :+ s
          }
      }
      return solicitacoes
  }
  
  def mudaStatus(oldSolicitacao : SolicitacaoFull, status : String): SolicitacaoFull = {
    val solicitacao = SolicitacaoFull(oldSolicitacao.id, oldSolicitacao.professor,
              oldSolicitacao.relator, oldSolicitacao.dataSolicitacao, 
              oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
              oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              status, oldSolicitacao.motivoCancelamento, 
              oldSolicitacao.dataJulgamentoAfast)
    return solicitacao
  }
  
  def turnSolicitacaoIntoSolicitacaoFull
  (oldSolicitacao : Solicitacao): SolicitacaoFull = {
    val professor = UserService.getUser(oldSolicitacao.idProfessor)
    var relator : Option[User] = None
    if (oldSolicitacao.idRelator == 0){
      relator = None
    }else{
      relator = UserService.getUser(oldSolicitacao.idRelator)
    }
    val solicitacao = SolicitacaoFull(oldSolicitacao.id, professor,
              relator, oldSolicitacao.dataSolicitacao, 
              oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
              oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              oldSolicitacao.status,oldSolicitacao.motivoCancelamento, 
              oldSolicitacao.dataJulgamentoAfast)
    return solicitacao
  }
  
  
  def turnSolicitacaoFullIntoSolicitacao
  (oldSolicitacao : SolicitacaoFull): Solicitacao = {
    val idProfessor = oldSolicitacao.professor.get.id
    var idRelator : Long = 0
    if (oldSolicitacao.relator == None){
      idRelator = 0
    }else{
      idRelator = oldSolicitacao.relator.get.id
    }
    val solicitacao = Solicitacao(oldSolicitacao.id, idProfessor,
              idRelator, oldSolicitacao.dataSolicitacao, 
              oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
              oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              oldSolicitacao.status,oldSolicitacao.motivoCancelamento, 
              oldSolicitacao.dataJulgamentoAfast)
    return solicitacao
  }
  
 def addRelator(oldSolicitacao : SolicitacaoFull, idRelator : Long): SolicitacaoFull = {
      val relator = UserService.getUser(idRelator)
      val solicitacao = SolicitacaoFull(oldSolicitacao.id, 
      oldSolicitacao.professor,
      relator, oldSolicitacao.dataSolicitacao, 
      oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
      oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
      oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
      oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
      oldSolicitacao.status,oldSolicitacao.motivoCancelamento, 
      oldSolicitacao.dataJulgamentoAfast)
    return solicitacao
  }
  
  def cancelaSolicitacao(oldSolicitacao : SolicitacaoFull): SolicitacaoFull = {
    val solicitacao = SolicitacaoFull(oldSolicitacao.id, 
      oldSolicitacao.professor,
      oldSolicitacao.relator, oldSolicitacao.dataSolicitacao, 
      oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
      oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
      oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
      oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
      StatusSolicitacao.Cancelada.toString(),oldSolicitacao.motivoCancelamento, 
      oldSolicitacao.dataJulgamentoAfast)
    return solicitacao
  }
  
    def busca(idProfessor : Long, status : String): Seq[SolicitacaoFull] = {
        var solicitacoesIdProfessor : Seq[SolicitacaoFull] = Seq()
        var solicitacoesStatus : Seq[SolicitacaoFull] = Seq()
        solicitacoesIdProfessor = listAllSolicitacoesBySolicitante(idProfessor)
        solicitacoesStatus = listAllSolicitacoesByStatus(status)    
        if(status.equals(StatusSolicitacao.Todos.toString()) && idProfessor == 0){
            return listAllSolicitacoes
        }
        if (idProfessor == 0){
            return solicitacoesStatus
        }
        if (status.equals(StatusSolicitacao.Todos.toString())){
            return solicitacoesIdProfessor
        } 
        return mergeListas(solicitacoesIdProfessor, solicitacoesStatus)
    }

}
