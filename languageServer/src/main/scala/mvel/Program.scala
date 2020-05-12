package mvel

import miksilo.languageServer.JVMLanguageServer
import miksilo.languageServer.server.SimpleLanguageBuilder

object Program extends JVMLanguageServer(Seq(
  SimpleLanguageBuilder("MVEL", MVELLanguage.language))) {
}
