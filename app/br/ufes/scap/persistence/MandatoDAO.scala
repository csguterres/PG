package br.ufes.scap.persistence

import br.ufes.scap.models.{Mandato,MandatoForm, MandatoTableDef}
import play.api.Play
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.data.Form
import play.api.data.Forms._

class MandatoDAO extends BaseDAO {
  
  def findByProfessor(idProfessor : Long): Future[Seq[Mandato]] 
  
  def listAll: Future[Seq[Mandato]] 

}
