package br.ufes.scap.models

import java.util.Date
import br.ufes.scap.services.MandatoService

@javax.inject.Singleton
object Global {

    var SESSION_KEY : Long = 0
    var SESSION_TIPO : String = ""
    var SESSION_MATRICULA: String = ""
    var SESSION_EMAIL : String = ""
    var SESSION_PASS : String = ""
    var SESSION_CHEFE : Boolean = false
    var CHEFE_ID : Long = 0
    
    def checaData(dataInicio : Date, dataFim : Date): Boolean = {
      if (dataInicio.after(dataFim)){
        return false
      }else{
        return true
      }
    }
    
    def isPresidenteOuVice(): Int ={
      val mandatos = MandatoService.getMandatoAtual()
      for (m <- mandatos){
        Global.CHEFE_ID = m.idProfessor
        if (Global.SESSION_KEY == m.idProfessor){
          Global.SESSION_CHEFE = true
          return 0
        }
      }
      Global.SESSION_CHEFE = false
      return 0
    }
  
}
