package br.ufes.scap.controllers

import br.ufes.scap.models.{Global, User, Mandato, Parecer, ParecerForm}
import play.api.mvc._
import br.ufes.scap.services.{MandatoService, UserService}
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import scala.concurrent.Future
import java.sql.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent._