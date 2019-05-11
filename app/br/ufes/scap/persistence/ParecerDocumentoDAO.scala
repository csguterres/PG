package br.ufes.scap.persistence

import br.ufes.scap.models.{ParecerDocumento, ParecerDocumentoForm, ParecerDocumentoTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

object PareceresDocumento {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val pareceresDocumento = TableQuery[ParecerDocumentoTableDef]
  
  def add(parecerDocumento: ParecerDocumento): Future[String] = {
    dbConfig.db.run(pareceresDocumento += parecerDocumento).map(res => 
      "Parecer successfully added").recover {
      case ex: Exception => 
        println(ex.printStackTrace())
        ex.getClass().getName()
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(pareceresDocumento.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[ParecerDocumento]] = {
    dbConfig.db.run(pareceresDocumento.filter(_.id === id).result.headOption)
  }
    
  def update(parecerDocumento: ParecerDocumento) : Future[String] = {
    dbConfig.db.run(pareceresDocumento.filter(_.id === parecerDocumento.id).update(parecerDocumento)).map(res => "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findBySolicitacao(idSolicitacao : Long): Future[Seq[ParecerDocumento]] = {
    dbConfig.db.run(pareceresDocumento.filter(_.idSolicitacao === idSolicitacao).result)
  }
  
  
  def listAll: Future[Seq[ParecerDocumento]] = {
    dbConfig.db.run(pareceresDocumento.result)
  }


}
