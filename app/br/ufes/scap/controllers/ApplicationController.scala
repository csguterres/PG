package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserForm}
import play.api.mvc._
import scala.concurrent.Future
import br.ufes.scap.services.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future

class ApplicationController extends Controller { 

  def index = Action.async { implicit request =>
    UserService.listAllUsers map { users =>
      Ok(br.ufes.scap.views.html.listUsers(UserForm.form, users))
    }
  }

  def addUser() = Action.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(0, data.nome, data.matricula, data.email, data.password, data.tipo)
        UserService.addUser(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    UserService.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }
  
  def editUser(id:Long) = Action.async { implicit request =>
    UserService.getUser(id) map { user =>
      Ok(br.ufes.scap.views.html.editUser(UserForm.form, user))
    }
  }
  
  def updateUser(id:Long) = Action.async { implicit request =>
    UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.addUser(errorForm, Seq.empty[User]))),
      data => {
        val newUser = User(id, data.nome, data.matricula, data.email, data.password, data.tipo)
        UserService.update(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      })
  }
}

