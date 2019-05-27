package br.ufes.scap.services

import br.ufes.scap.persistence.MandatoDAOSlick
import br.ufes.scap.models.Mandato
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import java.sql.Timestamp

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

  def listAllMandatos: Seq[Mandato] = {
    MandatoDAOSlick.listAll
  }
  
  def listAllMandatosByProfessor(idProfessor : Long): Seq[Mandato] = {
    MandatoDAOSlick.findByProfessor(idProfessor)
  }
    
  def update(mandato : Mandato) = { 
    MandatoDAOSlick.update(mandato)
  }
  
  def getMandatoAtual(): Seq[Mandato] = {
    val mandatos = this.listAllMandatos
    val dataAtual = new Timestamp(Calendar.getInstance().getTime().getTime())
    var mandatosList : Seq[Mandato] = Seq()
    for (m <- mandatos){
      if(dataAtual.after(m.dataIniMandato) && dataAtual.before(m.dataFimMandato)){
        mandatosList = mandatosList :+ m
      }
    }
    return mandatosList
  }
  
  def checaDataOutros(dataInicial : Date, dataFinal : Date, cargo : String): Boolean = {
    val mandatos = this.listAllMandatos
    for (m <- mandatos){
      if (!(dataFinal.before(m.dataIniMandato) || dataInicial.after(m.dataFimMandato))){
        if(m.cargo.equals(cargo)){
          return false
        }
      }
    }
    return true 
  }
  
}
