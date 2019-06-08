package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm, UserEditForm, 
 Global, TipoUsuario}
import play.api.mvc._
import br.ufes.scap.services.{UserService, AuthenticatorService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Success,Failure}

class UsersController @Inject()
(authenticatedUsuarioAction: AuthenticatedUsuarioAction,
    authenticatedSecretarioAction : AuthenticatedSecretarioAction,
    authenticatedProfessorAction : AuthenticatedProfessorAction)
    extends Controller { 

  def listarUsuarios = authenticatedSecretarioAction { implicit request =>
        val users = UserService.listAllUsersByTipo(TipoUsuario.Prof.toString())
        Ok(br.ufes.scap.views.html.listUsers(UserForm.form, users))
  }
  
  def addUserForm() = authenticatedSecretarioAction {
    Ok(br.ufes.scap.views.html.addUser(UserForm.form))
  }
  
  def addUser() =  authenticatedSecretarioAction.async { implicit request =>
      UserForm.form.bindFromRequest.fold(
        // if any error in submitted data
        errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm))),
        data => {
          val newUser = User(0, data.nome, data.matricula, data.email, data.password, data.tipo)
          UserService.addUser(newUser)
          Future.successful(Redirect(routes.UsersController.listarUsuarios()))
        })    
  }
    

  def deleteUser(id: Long) = authenticatedSecretarioAction { implicit request =>
      UserService.deleteUser(id) 
      Redirect(routes.UsersController.listarUsuarios())
  }
  
  def editUser(id:Long) = authenticatedUsuarioAction { implicit request =>
    val user = UserService.getUser(id)
    if (AuthenticatorService.isSecretario() || AuthenticatorService.isAutor(user.get)){
      Ok(br.ufes.scap.views.html.editUser(UserEditForm.form, user))
    }else{
      Ok(br.ufes.scap.views.html.erro())
    }
  }
  
  def updateUser(id:Long) = authenticatedUsuarioAction.async { implicit request =>
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

