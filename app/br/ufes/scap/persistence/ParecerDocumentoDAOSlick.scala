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
import scala.concurrent._
import scala.concurrent.duration._

object ParecerDocumentoDAOSlick extends ParecerDocumentoDAO {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val pareceresDocumento = TableQuery[ParecerDocumentoTableDef]
  
  @Override
  def  save(p: Any) = {
        val parecerDocumento = p.asInstanceOf[ParecerDocumento]
    dbConfig.db.run(pareceresDocumento += parecerDocumento).map(res => 
      "Parecer successfully added").recover {
      case ex: Exception => 
        println(ex.printStackTrace())
        ex.getClass().getName()
    }
  }

  @Override
  def delete(id: Long) = {
    dbConfig.db.run(pareceresDocumento.filter(_.id === id).delete)
  }

  @Override
  def get(id: Long): Option[ParecerDocumento] = {
    Await.result(dbConfig.db.run(pareceresDocumento.filter(_.id === id).result.headOption),Duration.Inf)
  }
    
  @Override
  def update(p: Any) = {
    val parecerDocumento = p.asInstanceOf[ParecerDocumento]
    dbConfig.db.run(pareceresDocumento.filter(_.id === parecerDocumento.id).update(parecerDocumento)).map(res => "Parecer successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def findBySolicitacao(idSolicitacao : Long): Seq[ParecerDocumento] = {
    Await.result(dbConfig.db.run(pareceresDocumento.filter(_.idSolicitacao === idSolicitacao).result),Duration.Inf)
  }
  
  
  def listAll: Seq[ParecerDocumento] = {
    Await.result(dbConfig.db.run(pareceresDocumento.result),Duration.Inf)
  }


}