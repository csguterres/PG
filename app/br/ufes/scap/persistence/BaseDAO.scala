package br.ufes.scap.persistence

import scala.concurrent.Future

trait BaseDAO{
  
	def save(T : Any): Future[String]
	
	def delete(id: Long): Future[Int]
	
	def get(id: Long): Future[Option[Any]]
	
	def update(T: Any): Future[String]
	
	def listAll(): Future[Seq[Any]]
}
