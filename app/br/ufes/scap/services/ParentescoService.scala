package br.ufes.scap.services

import br.ufes.scap.persistence.ParentescoDAOSlick
import br.ufes.scap.models.Parentesco

object ParentescoService {

  def addParentesco(parentesco: Parentesco) = {
    ParentescoDAOSlick.save(parentesco)
  }

  def deleteParentesco(id: Long) = {
    ParentescoDAOSlick.delete(id)
  }

  def getParentesco(id: Long): Option[Parentesco] = {
    ParentescoDAOSlick.get(id)
  }

  def listAllParentescos: Seq[Parentesco] = {
    ParentescoDAOSlick.listAll
  }
  
  def listAllParentescosByProfessor(idProfessor : Long): Seq[Parentesco] = {
    ParentescoDAOSlick.findByProfessor(idProfessor)
  }
    
  def update(parentesco : Parentesco) = { 
    ParentescoDAOSlick.update(parentesco)
  }
  
  def checaDiferente(id1 : Long, id2 : Long):Boolean = {
       return id1 != id2
  }
          
  def naoExisteParentesco(id1 : Long, id2 : Long):Boolean = {
          val parentescos = ParentescoService.listAllParentescosByProfessor(id1)
          for (p <- parentescos){
            if (p.idProfessor1 == id2 || p.idProfessor2 == id2){
              return false
            }
          }
          return true
        }
}
