# Contributing

Contributions are very welcome! It's worth searching 
[open issues](https://github.com/fractalwrench/json-2-kotlin/issues) first to ensure that
someone else isn't working on the problem.

If you plan on adding substantial new functionality, please open an issue first for discussion.

If you're interested in adding another option for source code generation, then the SourceBuildDelegate interface 
should get you started with API hooks. You can add JSON input files and expected Kotlin output under
 `test/resources/valid` - the parameterised test suite should automatically run these cases if they follow the 
 naming conventions. 
