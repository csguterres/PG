package br.ufes.scap.services

import br.ufes.scap.persistence.ParentescoDAOSlick
import br.ufes.scap.models.{Parentesco, ParentescoFull}

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

  def listAllParentescos: Seq[ParentescoFull] = {
    turnSeqParentescoIntoSeqParentescoFull(ParentescoDAOSlick.listAll)
  }
  
  def listAllParentescosByProfessor(idProfessor : Long): Seq[ParentescoFull] = {
    turnSeqParentescoIntoSeqParentescoFull(ParentescoDAOSlick.findByProfessor(idProfessor))
  }
    
  def update(parentesco : Parentesco) = { 
    ParentescoDAOSlick.update(parentesco)
  }
  
  def checaDiferente(id1 : Long, id2 : Long):Boolean = {
       return id1 != id2
  }
  
  def turnParentescoIntoParentescoFull(p : Parentesco) : ParentescoFull = {
      var user1 = UserService.getUser(p.idProfessor1)
      var user2 = UserService.getUser(p.idProfessor2)
      return new ParentescoFull(p.id, user1.get, user2.get)
  }
  
  def turnSeqParentescoIntoSeqParentescoFull(parentescos : Seq[Parentesco]) : Seq[ParentescoFull] = {
    var Parentescos : Seq[ParentescoFull] = Seq()
    for (p <- parentescos){
       Parentescos = Parentescos :+ turnParentescoIntoParentescoFull(p)
    }
    return Parentescos
  }
          
  def naoExisteParentesco(id1 : Long, id2 : Long):Boolean = {
          val parentescos = ParentescoService.listAllParentescosByProfessor(id1)
          for (p <- parentescos){
            if (p.professor1.id == id2 || p.professor2.id == id2){
              return false
            }
          }
          return true
        }
}
