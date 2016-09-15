# Technicalizer

This is just a small tool that takes an image and represents it as a circuit board-ish svg file.

## Compilation
Use the gradle wrapper by running

	./gradlew build

to build the tool. Afterwards, run the tool via

	java -jar build/libs/technicalizer.jar

and you'll get an error message about how you need to specify input and output files. Provide these as two command line parameters to the tool, i.e.

	java -jar build/libs/technicalizer.jar input.png output.svg

and you're done.
