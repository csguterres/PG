package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm, UserEditForm, UserLoginForm, Global}
import play.api.mvc._
import br.ufes.scap.services.{UserService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Success,Failure}
import scala.concurrent.duration._
import scala.concurrent._

class UsersController extends Controller { 

  def listarUsuarios = Action { implicit request =>
      if (AuthenticatorService.isSecretario()){
        val users = Await.result(UserService.listAllUsersByTipo("PROFESSOR"),Duration.Inf)
        Ok(br.ufes.scap.views.html.listUsers(UserForm.form, users))
      }else{
          Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }
  
  def addUser() =  Action.async { implicit request =>
    if(AuthenticatorService.isSecretario()){
      UserForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm, Seq.empty[User]))),
        data => {
          val newUser = User(0, data.nome, data.matricula, data.email, data.password, data.tipo)
          UserService.addUser(newUser).map(res =>
            Redirect(routes.UsersController.listarUsuarios())
          )
        })    
        }else{
          UserService.getUser(0) map(res =>
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    )}
  }
    

  def deleteUser(id: Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario()){
      UserService.deleteUser(id) 
      Redirect(routes.UsersController.listarUsuarios())
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def editUser(id:Long) = Action { implicit request =>
    if (AuthenticatorService.isSecretario() || id == Global.SESSION_KEY){
      val user = Await.result(UserService.getUser(id),Duration.Inf)
      Ok(br.ufes.scap.views.html.editUser(UserEditForm.form, user))
    }else{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
  }
  
  def updateUser(id:Long) = Action.async { implicit request =>
    UserEditForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.editUser(errorForm, None))),
      data => {
        val user = Await.result(UserService.getUser(id),Duration.Inf)
        val newUser = User(id, data.nome, user.get.matricula, data.email, data.password, user.get.tipo)
        UserService.update(newUser).map(res =>
          Redirect(routes.LoginController.menu())
        )
      })
  }
}

