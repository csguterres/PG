package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Parentesco, ParentescoFull, 
  User, UserForm, UserLoginForm, ParentescoForm, TipoUser}
import play.api.mvc._
import br.ufes.scap.services.{UserService, ParentescoService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import scala.collection.Seq
import java.util.Calendar
import java.text.SimpleDateFormat


class ParentescosController extends Controller { 
  
  def listarParentescos = Action { implicit request =>
      if (AuthenticatorService.isSecretario()){
        val parentescos = ParentescoService.listAllParentescos
        Ok(br.ufes.scap.views.html.listParentescos(parentescos))
      }else{
    		Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }

  def deleteParentesco(id: Long) = Action { implicit request =>
    if(AuthenticatorService.isSecretario()){
      ParentescoService.deleteParentesco(id)
      Redirect(routes.ParentescosController.listarParentescos())
    }else{
    	Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def addParentescoForm() = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      val users = UserService.listAllUsersByTipo(TipoUser.Prof.toString())
      Ok(br.ufes.scap.views.html.addParentesco(ParentescoForm.form,users))    
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def addParentesco() = Action.async { implicit request =>
    ParentescoForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => 
        Future.successful
        (BadRequest
          (br.ufes.scap.views.html.addParentesco
             (errorForm, 
                UserService.listAllUsersByTipo(TipoUser.Prof.toString())
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
