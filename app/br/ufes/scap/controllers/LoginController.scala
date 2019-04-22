package br.ufes.scap.controllers

import br.ufes.scap.models.{User, UserLogin, UserForm, UserLoginForm, Global}
import play.api.mvc._
import br.ufes.scap.services.UserService
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent._
import scala.concurrent.duration._
import play.api.data.Forms._
import play.api.data._
import scala.concurrent.Future

class LoginController extends Controller {

    def login() = Action.async { implicit request =>
    UserLoginForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(BadRequest(br.ufes.scap.views.html.userLogin(errorForm))),
      data => {
        val user = UserService.getUserByMatricula(data.matricula)
        val userReal = Await.result(user,Duration.Inf)
        Global.SESSION_KEY = userReal.get.id 
        Global.SESSION_TIPO = userReal.get.tipo
        Global.SESSION_MATRICULA = userReal.get.matricula
        if(Global.SESSION_TIPO.equals("SECRETARIO")){
          UserService.getUserByMatricula(data.matricula).map(res =>
            Ok(br.ufes.scap.views.html.menuSecretario(UserForm.form, userReal))
          )
        }else{
          UserService.getUserByMatricula(data.matricula).map(res =>
            Ok(br.ufes.scap.views.html.menuUsuario(UserForm.form, userReal))
          )
        }
      })
  }
    
    def notLoggedIn() = Action{
      Ok(br.ufes.scap.views.html.erro(UserLoginForm.form))
    }
    
    def logout() = Action {
        Global.SESSION_KEY = 0
        Global.SESSION_TIPO = ""
        Global.SESSION_MATRICULA = ""
        Redirect(routes.LoginController.login)
    }
    /*
    def notLoggedIn() = Action{
      Ok(br.ufes.scap.views.html.erro(UserForm.form))
    }
    
    */
    /*
    private val logger = play.api.Logger(this.getClass)

    //private val formSubmitUrl = routes.LoginController.processLoginAttempt
    
    //def showLoginForm = Action.async { implicit request =>
    //    Ok(br.ufes.scap.views.html.userLogin(UserLoginForm.form))
    //}
    
    def processLoginAttempt = Action.async { implicit request =>
        val errorFunction = { formWithErrors: Form[UserLogin] =>
            // form validation/binding failed...
            BadRequest(br.ufes.scap.views.html.userLogin(formWithErrors))
        }
        val successFunction = { user: UserLogin =>
            // form validation/binding succeeded ...
            Redirect(routes.LoginController.showLandingPage)
            //        .flashing("info" -> "You are logged in.")
            //        .withSession(Global.SESSION_USERNAME_KEY -> user.matricula)
        }
        val formValidationResult: Form[UserLogin] = UserLoginForm.form.bindFromRequest
        formValidationResult.fold(
            errorFunction,
            successFunction
        )
    }
    
    private val logoutUrl = routes.LoginController.logout
        
    def showLandingPage() = Action { implicit request: Request[AnyContent] =>
        Ok(views.html.loginLandingPage(logoutUrl))
    }
    
    def logout = Action { implicit request: Request[AnyContent] =>
        // docs: “withNewSession ‘discards the whole (old) session’”
        Redirect(routes.LoginController.showLoginForm)
        //    .flashing("info" -> "You are logged out.")
        //    .withNewSession
    }

		*/
}