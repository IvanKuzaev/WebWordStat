A test exercise for a backend workshop by Simbirsoft.

This project is created in IntelliJ Idea as a Maven project. It's the best choice to download the whole project to your computer and open it in IntelliJ Idea,
then run the WebWordStat.main(String[]) method. If you don't have this IDE, you can start the program manually, all *.class files have been
in advance compiled (in Java SE 12) and included in the github repository.

How to start a programm:

1) Download Maven build tool version 3.3.1 or later from: https://maven.apache.org/
2) Install Maven and ensure that Maven executables are specified in PATH environment variable
3) Change to the root project directory ...\WebWordStat-master. Enter in the command line the following command:

   mvn exec:java

This will start the program with default options: 16-24 Mb size of heap memory and a CLI argument of https://www.simbirsoft.com/. The specified site will be loaded
into memory by the program and the alphabetically sorted word statistic will be presented to you.

For further options enter in the project's root directory:

    mvn exec:java -Dexec.args="/help"

You will see the list of options of this program. Use these options, for example so:

    mvn exec:java -Dexec.args="/batchMode sites.txt"

If you want to run a crash test for this program, enter the following (replace C: for your disc drive):

    mvn exec:java -Dexec.args="file://localhost/C:./tiny_xerox_65536_times.html"

This will run a crash test with a 60Mb file for a "bottleneck" of 16-24 Mb of heap.

Or extract a file from tiny_xerox_2097152_times.rar into project directory and enter (replace C: for your disc drive):

    mvn exec:java -Dexec.args="file://localhost/C:./tiny_xerox_2097152_times.html"

This will run a crash test with a 1.87Gb file for a "bottleneck" of 16-24 Mb of heap.

Crash tests can take an amount of time, 5-10 minutes depending on your system.

In case of memory issues, try to increase the heap size in JVM options file ...\WebWordStat-master\.mvn\jvm.config and restart the program.
