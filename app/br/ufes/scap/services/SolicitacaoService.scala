package br.ufes.scap.services

import br.ufes.scap.persistence.SolicitacaoDAOSlick
import br.ufes.scap.models.{Solicitacao, SolicitacaoFull, 
  User, StatusSolicitacao, TipoAcessorio}
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.sql.Timestamp

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
 /*   var sol_final : Seq[Solicitacao] = Seq()
    for (s <- solicitacoes){
      if (!s.status.equals(StatusSolicitacao.Arquivada.toString())){
        sol_final = sol_final :+ s
      }
    }
    return turnSeqSolIntoSeqSolFull(sol_final) 
    
    */
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
              }
          }
          if(entra == true){
            solicitacoes = solicitacoes :+ s
          }
      }
      return solicitacoes
  }

  def mudaStatus(oldSolicitacao : SolicitacaoFull, status : String) = {
    val solicitacao = SolicitacaoFull(oldSolicitacao.id, oldSolicitacao.professor,
              oldSolicitacao.relator, oldSolicitacao.dataSolicitacao, 
              oldSolicitacao.dataIniAfast, oldSolicitacao.dataFimAfast, 
              oldSolicitacao.dataIniEvento, oldSolicitacao.dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              status, oldSolicitacao.motivoCancelamento)
    SolicitacaoService.update(solicitacao)
  }
  
  
  def turnSolicitacaoIntoSolicitacaoFull
  (oldSolicitacao : Solicitacao): SolicitacaoFull = {
    val professor = UserService.getUser(oldSolicitacao.idProfessor).get
    var relator : Option[User] = None
    if (oldSolicitacao.idRelator == 0){
      relator = None
    }else{
      relator = UserService.getUser(oldSolicitacao.idRelator)
    }
    val dataIniAfast = SharedServices.getData(oldSolicitacao.dataIniAfast)
    val dataFimAfast = SharedServices.getData(oldSolicitacao.dataFimAfast)
    val dataIniEvento = SharedServices.getData(oldSolicitacao.dataIniEvento)
    val dataFimEvento = SharedServices.getData(oldSolicitacao.dataFimEvento)
    
    val solicitacao = SolicitacaoFull(oldSolicitacao.id, professor,
              relator, oldSolicitacao.dataSolicitacao, 
              dataIniAfast, dataFimAfast, 
              dataIniEvento, dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              oldSolicitacao.status,oldSolicitacao.motivoCancelamento)
    return solicitacao
  }
  
  
  def turnSolicitacaoFullIntoSolicitacao
  (oldSolicitacao : SolicitacaoFull): Solicitacao = {
    val idProfessor = oldSolicitacao.professor.id
    var idRelator : Long = 0
    if (oldSolicitacao.relator == None){
      idRelator = 0
    }else{
      idRelator = oldSolicitacao.relator.get.id
    }
    val dataIniAfast = Timestamp.valueOf(oldSolicitacao.dataIniAfast.atStartOfDay())
    val dataFimAfast = Timestamp.valueOf(oldSolicitacao.dataFimAfast.atStartOfDay())
    val dataIniEvento = Timestamp.valueOf(oldSolicitacao.dataIniEvento.atStartOfDay())
    val dataFimEvento = Timestamp.valueOf(oldSolicitacao.dataFimEvento.atStartOfDay())
    val solicitacao = Solicitacao(oldSolicitacao.id, idProfessor,
              idRelator, oldSolicitacao.dataSolicitacao, 
              dataIniAfast, dataFimAfast, 
              dataIniEvento, dataFimEvento, 
              oldSolicitacao.nomeEvento, oldSolicitacao.cidade, 
              oldSolicitacao.onus, oldSolicitacao.tipoAfastamento, 
              oldSolicitacao.status,oldSolicitacao.motivoCancelamento)
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
      oldSolicitacao.status,oldSolicitacao.motivoCancelamento)
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
      StatusSolicitacao.Cancelada.toString(),oldSolicitacao.motivoCancelamento)
    return solicitacao
  }
  
    def busca(idProfessor : Long, idRelator : Long, status : String): Seq[SolicitacaoFull] = {
        var solicitacoesProfessor = listAllSolicitacoes
        var solicitacoesStatus = listAllSolicitacoes
        var solicitacoesRelator = listAllSolicitacoes

        if(!status.equals(TipoAcessorio.Todos.toString())){
          solicitacoesStatus = listAllSolicitacoesByStatus(status)
        }
        if (idProfessor != 0){
            solicitacoesProfessor = listAllSolicitacoesBySolicitante(idProfessor)
        }
        if (idRelator != 0){
            solicitacoesRelator = listAllSolicitacoesByRelator(idRelator)
        } 
        val solicitacoes = mergeListas(mergeListas(solicitacoesProfessor, solicitacoesStatus),solicitacoesRelator)
        return solicitacoes
    }
}
