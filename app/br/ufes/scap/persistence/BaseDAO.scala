package br.ufes.scap.persistence

import scala.concurrent.Future
import java.lang.Object

trait BaseDAO{
  
	def save(T : Any): Future[String]
	
	def delete(id: Long): Future[Int]
	
	def get(id: Long): Future[Option[Any]]
	
	def update(T: Any): Future[String]
	
}
