package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, MandatoForm, 
UserLoginForm,TipoUser}
import play.api.mvc._
import br.ufes.scap.services.{MandatoService, UserService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat

class MandatosController extends Controller {
  
  def showMandatosByProfessor(idProfessor : Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      val user = UserService.getUser(idProfessor)
      val mandatos = MandatoService.listAllMandatosByProfessor(idProfessor)
      Ok(br.ufes.scap.views.html.listMandatos(MandatoForm.form, mandatos, user))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }

  
  def addMandatoForm() = Action { implicit request =>
    if(AuthenticatorService.isSecretario()){
      val users = UserService.listAllUsersByTipo(TipoUser.Prof.toString())
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
          MandatoService.addMandato(newMandato)
          Future.successful( Redirect(routes.LoginController.menu()))
        })
  }
  
  def deleteMandato(id: Long) = Action { implicit request =>
      if (AuthenticatorService.isSecretario()){
        MandatoService.deleteMandato(id) 
        Redirect(routes.LoginController.menu())
      }else{
        Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }

  def editMandato(id:Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      val mandato = MandatoService.getMandato(id)
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
        val mandato = MandatoService.getMandato(id)
        val newMandato = Mandato(id, mandato.get.idProfessor, data.cargo, iniMandato, fimMandato)
        MandatoService.update(newMandato)
        Future.successful(  Redirect(routes.LoginController.menu())
        )
      })
  }
    
}
