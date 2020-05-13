package mvel

import miksilo.modularLanguages.core.deltas._
import miksilo.modularLanguages.core.deltas.grammars.{BodyGrammar, LanguageGrammars}
import miksilo.modularLanguages.core.deltas.path.PathRoot
import miksilo.modularLanguages.core.node.{Node, NodeField, NodeShape}
import miksilo.languageServer.core.language.{Compilation, Language}
import miksilo.modularLanguages.core.SolveConstraintsDelta
import miksilo.modularLanguages.deltas.bytecode.types.{ArrayTypeDelta, UnqualifiedObjectTypeDelta, VoidTypeDelta}
import miksilo.modularLanguages.deltas.javac.classes.skeleton.JavaClassDelta
import miksilo.modularLanguages.deltas.javac.methods.{AccessibilityFieldsDelta, MethodParameters}
import miksilo.modularLanguages.deltas.method.MethodDelta
import miksilo.modularLanguages.deltas.method.MethodDelta.Shape
import miksilo.modularLanguages.deltas.statement.{BlockDelta, LabelStatementDelta, StatementDelta}

object DroolsBlock extends DeltaWithGrammar with DeltaWithPhase
{
  object Shape extends NodeShape
  object Statements extends NodeField

  override def inject(language: Language): Unit = {
    super.inject(language)
    LabelStatementDelta.isLabelScope.add(language, Shape, ())
    SolveConstraintsDelta.constraintCollector.add(language, (compilation, builder) => {
      val block = compilation.program.asInstanceOf[PathRoot]
      BlockDelta.collectConstraints(compilation, builder, block, builder.newScope(debugName = "programScope"))
    })
  }

  override def transformGrammars(grammars: LanguageGrammars, state: Language): Unit = {
    import grammars._
    val statements = find(StatementDelta.Grammar).manyVertical.as(BlockDelta.Statements).asNode(Shape)
    find(BodyGrammar).inner = statements
  }

  override def transformProgram(program: Node, compilation: Compilation): Unit = {
  }

  //TODO bring back. override def dependencies: Set[Contract] = Set(ImplicitObjectSuperClass, MethodDelta)

  override def description: String = "Creates a language where the program is simply a Java block."

  override def dependencies: Set[Contract] = Set(BlockDelta)
}

