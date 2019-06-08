package br.ufes.scap.services

import br.ufes.scap.persistence.MandatoDAOSlick
import br.ufes.scap.models.{Mandato, MandatoFull}
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.sql.Timestamp
import java.util.Date

object MandatoService {

  def addMandato(mandato: Mandato) = {
    MandatoDAOSlick.save(mandato)
  }

  def deleteMandato(id: Long) = {
    MandatoDAOSlick.delete(id)
  }

  def getMandato(id: Long): Option[Mandato] = {
    MandatoDAOSlick.get(id)
  }
  
  def getMandatoFull(id: Long): MandatoFull = {
    return turnMandatoIntoMandatoFull(MandatoDAOSlick.get(id).get)
  }

  def listAllMandatos: Seq[MandatoFull] = {
    turnSeqMandatosIntoSeqMandatosFull(MandatoDAOSlick.listAll)
  }
  
  def listAllMandatosByProfessor(idProfessor : Long): Seq[MandatoFull] = {
    return turnSeqMandatosIntoSeqMandatosFull(MandatoDAOSlick.findByProfessor(idProfessor))
  }
    
  def update(mandato : Mandato) = { 
    MandatoDAOSlick.update(mandato)
  }
  
  def getMandatoAtual(): Seq[MandatoFull] = {
    val mandatos = this.listAllMandatos
    val dataAtual = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
    var mandatosList : Seq[MandatoFull] = Seq()
    for (m <- mandatos){
      if(dataAtual.isAfter(m.dataIniMandato) && dataAtual.isBefore(m.dataFimMandato)){
        mandatosList = mandatosList :+ m
      }
    }
    return mandatosList
  }
    
  def turnMandatoIntoMandatoFull(oldMandato : Mandato): MandatoFull ={
    val prof = UserService.getUser(oldMandato.idProfessor).get
    val dataIniMandato = SharedServices.getData(oldMandato.dataIniMandato)
    val dataFimMandato = SharedServices.getData(oldMandato.dataFimMandato)
    return MandatoFull(oldMandato.id, prof, oldMandato.cargo, 
        dataIniMandato, dataFimMandato)
  }
  
  def turnSeqMandatosIntoSeqMandatosFull(mandatos : Seq[Mandato]): Seq[MandatoFull] = {
    var mandatosFull : Seq[MandatoFull] = Seq()
    for (m <- mandatos){
      mandatosFull = mandatosFull :+ this.turnMandatoIntoMandatoFull(m)
    }
    return mandatosFull
  }
  
  def checaDataOutros(dataInicial : Date, dataFinal : Date, cargo : String): Boolean = {
    var dataIni = new java.sql.Date(dataInicial.getTime()).toLocalDate();
    var dataFim = new java.sql.Date(dataInicial.getTime()).toLocalDate();
    val mandatos = this.listAllMandatos
    for (m <- mandatos){
      if (!(dataFim.isBefore(m.dataIniMandato) || dataIni.isAfter(m.dataFimMandato))){
        if(m.cargo.equals(cargo)){
          return false
        }
      }
    }
    return true 
  }
  
}
