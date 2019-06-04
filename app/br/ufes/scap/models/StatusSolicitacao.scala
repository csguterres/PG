package br.ufes.scap.models

object StatusSolicitacao extends Enumeration {
  type StatusSolicitacao = Value
  val Iniciada = Value("INICIADA")
  val Bloqueada = Value("BLOQUEADA")
  val Liberada = Value("LIBERADA")
  val AprovadaDI = Value("APROVADA-DI")
  val AprovadaCT = Value("APROVADA-CT")
  val AprovadaPRPPG = Value("APROVADA-PRPPG")
  val Cancelada = Value("CANCELADA")
  val Arquivada = Value("ARQUIVADA")
  val Reprovada = Value("REPROVADA")
}