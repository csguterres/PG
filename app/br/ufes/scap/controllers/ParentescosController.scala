package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Parentesco, ParentescoFull, 
  User, UserForm, ParentescoForm, TipoUsuario}
import play.api.mvc._
import br.ufes.scap.services.{UserService, ParentescoService, 
  AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import scala.collection.Seq
import java.util.Calendar
import java.text.SimpleDateFormat

class ParentescosController @Inject() 
(authenticatedUsuarioAction: AuthenticatedUsuarioAction,
    authenticatedSecretarioAction : AuthenticatedSecretarioAction) extends Controller { 
  
  def listarParentescos = authenticatedUsuarioAction { 
        val parentescos = ParentescoService.listAllParentescos
        Ok(br.ufes.scap.views.html.listParentescos(parentescos))
  }

  def deleteParentesco(id: Long) = authenticatedSecretarioAction { implicit request =>
    if(AuthenticatorService.isSecretario()){
      ParentescoService.deleteParentesco(id)
      Redirect(routes.ParentescosController.listarParentescos())
    }else{
    	Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def addParentescoForm() = authenticatedSecretarioAction { implicit request =>
      val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
      Ok(br.ufes.scap.views.html.addParentesco(ParentescoForm.form,users))    
  }
  
  def addParentesco() = authenticatedSecretarioAction.async { implicit request =>
    ParentescoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => 
        Future.successful
        (BadRequest
          (br.ufes.scap.views.html.addParentesco
             (errorForm, 
                UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
             )
           )
        ),
      data => {
        val newParentesco = Parentesco(0, data.idProfessor1, data.idProfessor2)
        ParentescoService.addParentesco(newParentesco)
        Future.successful(Redirect(routes.ParentescosController.listarParentescos()))
      })
  }
 
}
