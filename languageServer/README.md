This project defines an LSP server for CloudFormation Templates (CFT). It supports cross-platform compilation through [sbt-crossproject](https://github.com/portable-scala/sbt-crossproject), allowing it to run on both Node and the JVM. There is a small amount of Node specific code is in the `js` folder, and there are tests that run on the JVM located in the `jvm` folder. Almost all the code is shared between the two runtimes and is located in the `shared` folder.

The CloudFormation Template LSP server is defined using [Miksilo](https://github.com/keyboardDrummer/Miksilo), a library that simplifies defining languages and related tooling such as LSP servers. To use Miksilo, a _language definition_ must be specified. The language definition for CFT is located in [CloudFormationLanguage.scala](shared/src/main/scala/cloudformation/CloudFormationLanguage.scala), where both a JSON and YAML variant of the language are specified. The language definitions for JSON and YAML CFT are based on JSON and YAML language definitions taken from the Miksilo library, while language aspects specific to CloudFormation Templates are specified in [CloudFormationTemplate.scala](shared/src/main/scala/cloudformation/CloudFormationTemplate.scala)

To enable optimal code re-use, the YAML variant of the CFT language definition will, after parsing the YAML, apply the transformations in [ConvertObjectMemberKeysToStrings.scala](shared/src/main/scala/cloudformation/ConvertObjectMemberKeysToStrings.scala) and [ConvertTagsToObjectDelta.scala](shared/src/main/scala/cloudformation/ConvertTagsToObjectDelta.scala) to transform the YAML to JSON, after which it uses the code shared with the JSON variant from [CloudFormationTemplate.scala](shared/src/main/scala/cloudformation/CloudFormationTemplate.scala).