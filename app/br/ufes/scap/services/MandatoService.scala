package br.ufes.scap.services

import br.ufes.scap.persistence.Mandatos
import br.ufes.scap.models.Mandato
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import java.sql.Timestamp

object MandatoService {

  def addMandato(mandato: Mandato): Future[String] = {
    Mandatos.add(mandato)
  }

  def deleteMandato(id: Long): Future[Int] = {
    Mandatos.delete(id)
  }

  def getMandato(id: Long): Future[Option[Mandato]] = {
    Mandatos.get(id)
  }

  def listAllMandatos: Future[Seq[Mandato]] = {
    Mandatos.listAll
  }
  
  def listAllMandatosByProfessor(idProfessor : Long): Future[Seq[Mandato]] = {
    Mandatos.findByProfessor(idProfessor)
  }
    
  def update(mandato : Mandato): Future[String] = { 
    Mandatos.update(mandato)
  }
  
  def getMandatoAtual(): Seq[Mandato] = {
    val mandatos = Await.result(this.listAllMandatos, Duration.Inf)
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
    val mandatos = Await.result(this.listAllMandatos,Duration.Inf)
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
