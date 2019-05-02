package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, Parentesco, ParentescoFull, User, UserForm, UserLoginForm, ParentescoForm}
import play.api.mvc._
import br.ufes.scap.services.{UserService, ParentescoService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import scala.collection.Seq
import java.util.Calendar
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._


class ParentescosController extends Controller { 
  
  def index = Action { implicit request =>
      if (Global.isSecretario()){
        val parentescos = Await.result(ParentescoService.listAllParentescos, Duration.Inf)
        var Parentescos : Seq[ParentescoFull] = Seq()
        for (p <- parentescos){
          var user1 = Await.result(UserService.getUser(p.idProfessor1),Duration.Inf)
          var user2 = Await.result(UserService.getUser(p.idProfessor2),Duration.Inf)
          var P = new ParentescoFull(p.id, user1, user2)
          Parentescos = Parentescos :+ P
        }
        Ok(br.ufes.scap.views.html.listParentescos(ParentescoForm.form, Parentescos))
      }else{
    		Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }

  def deleteParentesco(id: Long) = Action { implicit request =>
    if(Global.isSecretario()){
      ParentescoService.deleteParentesco(id)
      Redirect(routes.ParentescosController.index())
    }else{
    	Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def addParentescoPre() = Action { implicit request =>
    if (Global.isSecretario()){
      val users = Await.result(UserService.listAllUsersByTipo("PROFESSOR"),Duration.Inf)
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
                (Await.result
                  (UserService.listAllUsersByTipo("PROFESSOR"),Duration.Inf)
                )
             )
           )
        ),
      data => {
        val newParentesco = Parentesco(0, data.idProfessor1, data.idProfessor2)
        ParentescoService.addParentesco(newParentesco).map(res =>
          Redirect(routes.ParentescosController.index())
        )
      })
  }
 
}