package mvel

import miksilo.editorParser.SourceUtils
import miksilo.editorParser.parsers.editorParsers.{SourceRange, UntilBestAndXStepsStopFunction}
import miksilo.languageServer.core.textMate.GenerateTextMateGrammar
import miksilo.languageServer.server.{LanguageServerTest, MiksiloLanguageServer}
import miksilo.lspprotocol.lsp.{CompletionList, FileRange, HumanPosition}
import miksilo.modularLanguages.core.bigrammar.BiGrammarToParser
import miksilo.modularLanguages.util.{TestLanguageBuilder, TestingLanguage}
import org.scalatest.funsuite.AnyFunSuite

class MVELTest extends AnyFunSuite with LanguageServerTest {

  val language: TestingLanguage = TestLanguageBuilder.buildWithParser(MVELLanguage.deltas,
    UntilBestAndXStepsStopFunction(1))
  val server = new MiksiloLanguageServer(language)

  test("generate textmate grammar") {
    val output = GenerateTextMateGrammar.toTextMate(BiGrammarToParser)(BiGrammarToParser.toParserBuilder(language.grammars.root))
    System.out.append(output)
  }

  test("No diagnostics") {
    val program = SourceUtils.getResourceFileContents("example.mvel")
    val result = getDiagnostics(server, program)
    assert(result.isEmpty)
  }

  test("Diagnostics edited") {
    val program = """i (taxEventsEffectiveWindowStartDateUTC < firstFilingStartDate){
                    |	taxEventsEffectiveWindowStartDateUTC = firstFilingStartDate;
                    |}""".stripMargin
    val result = getDiagnostics(server, program)
    assert(result.size == 2)
  }

  test("Goto definition") {
    val program = SourceUtils.getResourceFileContents("example.mvel")
    val result: Seq[FileRange] = gotoDefinition(server, program, new HumanPosition(437, 36))
    assertResult(SourceRange(new HumanPosition(42,6), new HumanPosition(42,17)))(result.head.range)
  }

  test("Code completion parameter") {
    val program = SourceUtils.getResourceFileContents("example.mvel")
    val result = complete(server, program, new HumanPosition(437, 38))
    val item = createCompletionItem("SSHLocation")
    assertResult(CompletionList(isIncomplete = false, Seq(item)))(result)
  }

  test("Parse example") {
    val json = TestLanguageBuilder.buildWithParser(MVELLanguage.deltas)
    val source = SourceUtils.getResourceFileContents("example.mvel")
    json.compileString(source)
  }

}
