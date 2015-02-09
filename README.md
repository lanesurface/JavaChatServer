JIM Server
==========
To configure...
1. Open a new CMD window with Java added to your build path
2. Type: cd (the directory to your download from your cwd)/bin
3. Type java Server 10000 (10000 is currently the only port that JIM Client looks for)
4. Open another CMD and type: ipconfig. Look for the line that says gateway and copy down the address (should look something like 108.73.89.75)
5. Port forward your router to your server with a public port of 10000 and private port of 10000
6. Connect with a JIM client to the ip address of your gateway.