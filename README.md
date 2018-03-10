# Json2Kotlin

Documentation under construction 🔨


## Running
Run `./gradlew bootRun` and visit http://localhost:8080

## Releasing
### Command Line
Run `./gradlew assembleDist`, unzip the archive, and navigate to `bin`.

Then specify the json file, e.g.
`./cmdline -input /c/Users/<User>/json2kotlin/bin/test.json`

### Spring
Deploy the JAR to AWS using `./gradlew bootRepackage`
