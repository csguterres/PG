package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm, UserEditForm, 
UserLoginForm, Global, TipoUser}
import play.api.mvc._
import br.ufes.scap.services.{UserService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Success,Failure}

class UsersController extends Controller { 

  def listarUsuarios = Action { implicit request =>
      if (AuthenticatorService.isSecretario()){
        val users = UserService.listAllUsersByTipo(TipoUser.Prof.toString())
        Ok(br.ufes.scap.views.html.listUsers(UserForm.form, users))
      }else{
          Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
      }
  }
  
  def addUser() =  Action.async { implicit request =>
    if(AuthenticatorService.isSecretario()){
      UserForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm))),
        data => {
          val newUser = User(0, data.nome, data.matricula, data.email, data.password, data.tipo)
          UserService.addUser(newUser)
          Future.successful(Redirect(routes.UsersController.listarUsuarios()))
        })    
     }else{
        Future.successful(Ok(br.ufes.scap.views.html.erro(UserLoginForm.form)))
     }
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
    val user = UserService.getUser(id)
    if (AuthenticatorService.isSecretario() || AuthenticatorService.isAutor(user)){
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
        val user = UserService.getUser(id)
        val newUser = User(id, data.nome, user.get.matricula, data.email, data.password, user.get.tipo)
        UserService.update(newUser)
        Future.successful(  Redirect(routes.LoginController.menu())
        )
      })
  }
}

