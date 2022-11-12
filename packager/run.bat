if "%1" == "" start "" /min "%~f0" MY_FLAG && exit
set CURRENT_DIR=%cd%
set JAVA_HOME=jdk
set set Path=%JAVA_HOME%\bin;%Path%
call java -jar -Djava.library.path=lib/native presenter-swing.jar
exit