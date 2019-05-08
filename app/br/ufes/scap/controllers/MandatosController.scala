package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, MandatoForm, UserLoginForm}
import play.api.mvc._
import br.ufes.scap.services.{MandatoService, UserService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._

class MandatosController extends Controller {
    
  /*
  def index = Action { implicit request =>
      if (Global.isSecretario()){
        val mandatos = Await.result(MandatoService.listAllMandatos, Duration.Inf)
        Ok(br.ufes.scap.views.html.listMandatos(MandatoForm.form, mandatos))
      }else{
       Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
     }
  }
  */
  
  def showMandatosByProfessor(idProfessor : Long) = Action { implicit request =>
    if (Global.isSecretario()){
      val user = Await.result(UserService.getUser(idProfessor),Duration.Inf)
      val mandatos = Await.result(MandatoService.listAllMandatosByProfessor(idProfessor), Duration.Inf)
      Ok(br.ufes.scap.views.html.listMandatos(MandatoForm.form, mandatos, user))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }

  
  def addMandatoPre() = Action { implicit request =>
    if(Global.isSecretario()){
      val users = Await.result(UserService.listAllUsersByTipo("PROFESSOR"), Duration.Inf)
      Ok(br.ufes.scap.views.html.addMandato(MandatoForm.form, users))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def addMandato() = Action.async { implicit request =>
        MandatoForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => 
          Future.successful(BadRequest(br.ufes.scap.views.html.addMandato(errorForm, Seq.empty[User]))),
        data => {
          val iniMandato = new Timestamp(data.dataIniMandato.getTime())
          val fimMandato = new Timestamp(data.dataFimMandato.getTime())
          val newMandato = Mandato(0, data.idProfessor, data.cargo, iniMandato, fimMandato)
          MandatoService.addMandato(newMandato).map(res =>
            Redirect(routes.LoginController.menu())
          )
        })
  }
  
  def deleteMandato(id: Long) = Action { implicit request =>
      if (Global.isSecretario()){
        MandatoService.deleteMandato(id) 
        Redirect(routes.LoginController.menu())
      }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }

  def editMandato(id:Long) = Action { implicit request =>
    if (Global.isSecretario()){
      val mandato = Await.result(MandatoService.getMandato(id), Duration.Inf)
      Ok(br.ufes.scap.views.html.editMandato(MandatoForm.form, mandato))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def updateMandato(id:Long) = Action.async { implicit request =>
      MandatoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => 
        Future.successful(BadRequest(br.ufes.scap.views.html.editMandato(errorForm, None))),
      data => {
        val iniMandato = new Timestamp(data.dataIniMandato.getTime())
        val fimMandato = new Timestamp(data.dataFimMandato.getTime())
        val mandato = Await.result(MandatoService.getMandato(id), Duration.Inf)
        val newMandato = Mandato(id, mandato.get.idProfessor, data.cargo, iniMandato, fimMandato)
        MandatoService.update(newMandato).map(res =>
          Redirect(routes.LoginController.menu())
        )
      })
  }
    
}
