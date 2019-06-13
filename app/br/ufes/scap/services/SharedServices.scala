package br.ufes.scap.services

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.sql.Timestamp
import java.util.Date
import br.ufes.scap.models.Global

object SharedServices {
  
  def getData(data : Timestamp): LocalDate = {
    return LocalDateTime.ofInstant(data.toInstant(), ZoneId.of("America/Sao_Paulo")).toLocalDate()
  }
  
  def getDataTime(data : Timestamp): LocalDateTime = {
    return LocalDateTime.ofInstant(data.toInstant(), ZoneId.of("America/Sao_Paulo"))
  }
  
  def checaDataFromSolicitacao(dataIni : Date, idSolicitacao : Long): Boolean = {
    val sol = SolicitacaoService.getSolicitacao(idSolicitacao)
    val dataLimite = java.sql.Date.valueOf(sol.dataIniAfast)
    return checaData(dataIni,dataLimite) 
  }
  
  def checaData(dataInicio : Date, dataFim : Date): Boolean = {
      if (dataInicio.after(dataFim)){
        return false
      }else{
        return true
      }
    }
    
    def isPresidenteOuVice() ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        Global.CHEFE_ID = m.professor.id
        if (Global.SESSION_KEY == m.professor.id){
          Global.SESSION_CHEFE = true
        }
      }
    }
}
