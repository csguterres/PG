package br.ufes.scap.persistence

trait BaseDAO{
  
	def save(T : Any)
	
	def delete(id: Long)
	
	def get(id: Long): Option[Any]
	
	def update(T: Any)
	
	def listAll(): Seq[Any]
}
