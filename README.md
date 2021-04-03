# Json2Kotlin

Converts a JSON string into Kotlin data classes.

## About

As you've probably guessed by now, this project automatically converts JSON to Kotlin source files. 
The tool itself is implemented 100% in Kotlin, and makes heavy use of Square's excellent [KotlinPoet](https://github.com/square/kotlinpoet) library.
        
Read [the blog post](blog/BLOG_POST.md) which covers how this multi-platform tool was written.

View [the source](https://github.com/fractalwrench/json-2-kotlin/tree/master/core/src/main/kotlin/com/fractalwrench/json2kotlin) to suggest improvements.

Grab the command line tool from [the latest release](https://github.com/fractalwrench/json-2-kotlin/releases/latest).

Get in touch [on Twitter](https://twitter.com/fractalwrench) if you have questions, comments, or simply enjoy high-quality cat gifs.


## Running
Run `./gradlew bootRun` and visit http://localhost:8080

## Releasing
### Command Line
Run `./gradlew assembleDist`, unzip the archive, and navigate to `bin`.

Then specify the json file, e.g.
`./cmdline -input /c/Users/<User>/json2kotlin/bin/test.json`

### Spring
Deploy the JAR to AWS using `./gradlew bootRepackage`
