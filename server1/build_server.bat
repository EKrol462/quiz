:: Updated bat files to resolve typing issues in the command line

:: Compile the client program
call ant -Darg0="127.0.0.1" -Darg1="7775" -f build_client.xml jar

:: compile the server program
call ant -Darg0="7775" -f build_server.xml jar

:: Run the server program, pass port as an argument.
java -jar run_server/jar/Server.jar 7775

pause;