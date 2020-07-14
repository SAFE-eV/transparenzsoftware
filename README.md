# Install and run
Be sure that maven is installed [https://maven.apache.org/](https://maven.apache.org/). To
package the application run:
```bash
mvn clean package
``` 

This will create an executable jar in the target folder `transparenzsoftware.jar`.

If you encounter an error like `Execution default-test of goal org.apache.maven.plugins:maven-surefire-plugin:2.18.1:test failed: The forked VM terminated without properly saying goodbye. VM crash or System.exit called?`
set `export _JAVA_OPTIONS=-Djdk.net.URLClassPath.disableClassPathURLCheck=true`, this is a known
issue in the current openjdk version.

Run with: 
```bash
java -jar target/transparenzsoftware.jar
```
  

  
## Options
* `-v` enables the logging
* `-cli` runs it as cli app
* `-f <path>` verifies the file runned in cli
* `-h` prints help text

## Development
In IntelliJ open the project as a maven project (choose the `pom.xml` file).

### Krypto stuff for testing 
Create a public key from the commandline:
```bash
openssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-192  -pkeyopt ec_param_enc:named_curve
openssl openssl ecparam -name secp192r1 -out pvk_file.pem
openssl ec -in pvk_file.pem -pubout -out public_key_file.pem
```

## Basic structure
The main entry point of the application is the `App` class. There is the 
static main function which does parameter parsing and initializing and starting 
the app.

The application has 4 major packages.  
* `gui` this holds the `Java Swing` classes for rendering the gui. The main entry
 point in this package is the `TransparenzSoftwareMain` class which holds, all 
 the components and event listeners.
* `i18n` holds a class wrapping the loading of translated strings
* `output` contains classes for generating the resulting xml
* `verifification` contains the logic to load and verify all implemented formats
