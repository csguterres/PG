package br.ufes.scap.services

import br.ufes.scap.persistence.Mandatos
import br.ufes.scap.models.Mandato
import scala.concurrent._
import scala.concurrent.duration._
import java.util.Date

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
