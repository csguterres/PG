package br.ufes.scap.controllers

import br.ufes.scap.models.{User, Mandato, MandatoFull, MandatoForm, 
TipoUsuario, Global}
import play.api.mvc._
import br.ufes.scap.services.{MandatoService, UserService, 
  AuthenticatorService, AuthenticatedUsuarioAction, 
  AuthenticatedProfessorAction, AuthenticatedSecretarioAction}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat

class MandatosController extends Controller {
  
  def showMandatosByProfessor(idProfessor : Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      val mandatos = MandatoService.listAllMandatosByProfessor(idProfessor)
      Ok(br.ufes.scap.views.html.listMandatos(mandatos, Global.SESSION_TIPO))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def listarMandatos() = Action { implicit request =>
      val mandatos = MandatoService.listAllMandatos
      Ok(br.ufes.scap.views.html.listMandatos(mandatos, Global.SESSION_TIPO))
  }

  
  def addMandatoForm() = Action { implicit request =>
    if(AuthenticatorService.isSecretario()){
      val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
      Ok(br.ufes.scap.views.html.addMandato(MandatoForm.form, users))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def addMandato() = Action.async { implicit request =>
        MandatoForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => 
          Future.successful
          (BadRequest(br.ufes.scap.views.html.addMandato
              (errorForm, UserService.listAllUsersByTipo(TipoUsuario.Prof.toString()))
              )
          ),
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
        Ok(br.ufes.scap.views.html.erro())
      }
  }
/*
  def editMandato(id:Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      val mandato = MandatoService.getMandatoFull(id)
      val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
      Ok(br.ufes.scap.views.html.editMandato(MandatoForm.form, mandato))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def updateMandato(id:Long) = Action.async { implicit request =>
      val mandato = MandatoService.getMandatoFull(id)
      val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
      MandatoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => 
        Future.successful(BadRequest(br.ufes.scap.views.html.editMandato(errorForm, mandato))),
      data => {
        val iniMandato = new Timestamp(data.dataIniMandato.getTime())
        val fimMandato = new Timestamp(data.dataFimMandato.getTime())
        val newMandato = Mandato(id, mandato.professor.id, data.cargo, iniMandato, fimMandato)
        MandatoService.update(newMandato)
        Future.successful(  Redirect(routes.LoginController.menu())
        )
      })
  }
  */  
}
