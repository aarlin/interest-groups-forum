# SO WHAT DO WE WANT TO DO FOR THIS SERVER?
# WHAT DO WE DO FOR SERVER AND NOT FOR CLIENT?
# WE WANT TO CREATE A SERVER THAT WILL LOGIN CLIENTS THESE CLIENTS 

from socket import *
from time import *
from threading import *

# SERVER IS STARTED AND WAITS AT A KNOWN PORT FOR REQUESTS FROM CLIENTS
# SHOULD IT BE (!TCP!) OR UDP CONNECTION?
serverPort = 12001 # HARD CODE SERVER PORT
serverSocket = socket(AF_INET, SOCK_STREAM) # CREATING SOCKET AND BINDING IT TO PORT
serverSocket.bind(('', serverPort))
serverSocket.listen(100)  # ONLY LISTEN TO 100 CLIENTS

while True:
    print('The server is setup and ready to receive from client\n')
    connectionSocket, addr = serverSocket.accept()
##      connectionSocket.send('===========================\r\n')
##      connectionSocket.send('|| Interest Groups Server ||\r\n')
##      connectionSocket.send('===========================\r\n')
##      connectionSocket.send('Awaiting commands...\r\n')
    send_prompt = connectionSocket.send("CAN WE SEND?d\n")
    received_data = connectionSocket.recv(128)  # RECEIVE LOGIN COMMAND
    commands = received_data.split(" ")

    print(commands[0])  # SHOULD BE FIRST command

    while (commands[0] == "ag"):
        display = 5
        try:
            if not commands[1]:
                display = commands[1]
        except IndexError:
            pass

        allgroups = "1. ( ) comp.programming \n2. ( ) comp.os.threads \n3. ( ) comp.lang.c \n4. (s) comp.lang.python \n5. (s) comp.lang.javascript \n"

        connectionSocket.send(allgroups.encode('UTF-8'))
        
        ag_data = connectionSocket.recv(128)
        ag_subcommands = ag_data.split(" ")
        while (ag_subcommands[0] != "q"): 
            if (ag_subcommands[0] == "s"):
                print("S")
                connectionSocket.send("AG 200 OK")
            elif (ag_subcommands[0] == "u"):
                print("U")
                connectionSocket.send("AG 200 OK")
            elif (ag_subcommands[0] == "n"):
                print("N")
                connectionSocket.send("AG 200 OK")
            elif (ag_subcommands[0] == "q"):
                print("SENT THE 200 OK")
                print("Q")
                commands[0] = ""
                connectionSocket.send("AG 200 OK")
            continue
    while (commands[0] == "sg"):
        display = 5
        try:
            if not commands[1]:
                display = commands[1]
        except IndexError:
            pass

        connectionSocket.send("1.  18  comp.programming \n" +
                              "2.   2  comp.lang.c \n" +
                              "3.   3  comp.lang.python \n" +
                              "4.  27  sci.crpyt \n" +
                              "5.      rec.arts.ascii \n")
        continue
    while (commands[0] == "rg"):
        display = 5
        try:
            if not commands[2]:
                display = commands[2]
        except IndexError:
            pass
        try:
            if not commands[1]:
                gname = commands[1]
        except IndexError:
            connectionSocket.send("Require a mandatory argument 'gname'\n")
            continue
            
        connectionSocket.send("1. N  Nov 12 19:32:02   Sort a Python dictionary by value \n" +
                              "2. N  Nov 11 08:11:34   How to print to stderr in Python? \n" +
                              "3. N  Nov 10 22:05:47   \"Print\" and \"Input\" in one file \n" +
                              "4.    Nov  9 13:59:05   How not to display the user inputs? \n" +
                              "5.    Nov  9 12:46:10   Declaring custom exceptions \n")

        continue                
    if (commands[0] == "logout"):
        connectionSocket.send('Closing your connection\r\n')
        print('Closing connection to client\n')
        connectionSocket.close()                       

           
serverSocket.close()            
    
    

