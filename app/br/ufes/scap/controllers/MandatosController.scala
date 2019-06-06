package br.ufes.scap.controllers

import br.ufes.scap.models.{User, Mandato, MandatoFull, MandatoForm, 
TipoUsuario, Global}
import play.api.mvc._
import br.ufes.scap.services.{MandatoService, UserService, 
  AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat

class MandatosController @Inject() 
(authenticatedUsuarioAction: AuthenticatedUsuarioAction,
    authenticatedSecretarioAction : AuthenticatedSecretarioAction, 
    authenticatedProfessorAction : AuthenticatedProfessorAction) extends Controller {
  
  def showMandatosByProfessor(idProfessor : Long) = authenticatedUsuarioAction { implicit request =>
      val mandatos = MandatoService.listAllMandatosByProfessor(idProfessor)
      Ok(br.ufes.scap.views.html.listMandatos(mandatos, Global.SESSION_TIPO))
  }
  
  def listarMandatos() = authenticatedUsuarioAction { implicit request =>
      val mandatos = MandatoService.listAllMandatos
      Ok(br.ufes.scap.views.html.listMandatos(mandatos, Global.SESSION_TIPO))
  }

  
  def addMandatoForm() = authenticatedSecretarioAction { implicit request =>
      val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
      Ok(br.ufes.scap.views.html.addMandato(MandatoForm.form, users))
  }
  
  def addMandato() = authenticatedSecretarioAction.async { implicit request =>
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
          Future.successful(Redirect(routes.MandatosController.listarMandatos()))
        })
  }
  
  def deleteMandato(id: Long) = authenticatedSecretarioAction { implicit request =>
        MandatoService.deleteMandato(id) 
        Redirect(routes.MandatosController.listarMandatos())
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
