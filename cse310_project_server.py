# SO WHAT DO WE WANT TO DO FOR THIS SERVER?
# WHAT DO WE DO FOR SERVER AND NOT FOR CLIENT?
# WE WANT TO CREATE A SERVER THAT WILL LOGIN CLIENTS THESE CLIENTS 

##      connectionSocket.send('===========================\r\n')
##      connectionSocket.send('|| Interest Groups Server ||\r\n')
##      connectionSocket.send('===========================\r\n')
##      connectionSocket.send('Awaiting commands...\r\n')

from socket import *
from time import *
from threading import *
from json import *
from pprint import *
from re import *

# SERVER IS STARTED AND WAITS AT A KNOWN PORT FOR REQUESTS FROM CLIENTS
# SHOULD IT BE (!TCP!) OR UDP CONNECTION?
serverPort = 12001 # HARD CODE SERVER PORT
serverSocket = socket(AF_INET, SOCK_STREAM) # CREATING SOCKET AND BINDING IT TO PORT
serverSocket.bind(('', serverPort))
serverSocket.listen(100)  # ONLY LISTEN TO 100 CLIENTS

ag_flag = 0
sg_flag = 0
rg_flag = 0

numelements = 0
with open('server.json') as serverfile:
    server_data = load(serverfile)
    for n in server_data['discussion_groups']:
        numelements += 1


print('The server is setup and ready to receive from client\n')
connectionSocket, addr = serverSocket.accept()

while True:
    print('Waiting for user command')
    # with open('server.json', 'rb') as f:
    #     connectionSocket.sendall(f.read())

    received_data = connectionSocket.recv(128)  # command
    commands = received_data.split()
    print(commands)

    if (commands[0] == "ag"):
        print('Entered command state of ag')
        default_display = 5
        try:
            if commands[1] != "":
                default_display = int(commands[1])
        except IndexError:
            pass

        ag_info = ""
        with open('server.json') as serverfile:
            server_data = load(serverfile)
            for i in range(default_display):
                ag_info += server_data['discussion_groups'][i]['groupname']
                ag_info += '\n'
        connectionSocket.sendall(ag_info.encode('UTF-8'))

        while (ag_flag == 0):
            print('Entered subcommands state of ag')
            ag_data = connectionSocket.recv(128)
            ag_subcommands = ag_data.split(" ")

            if (ag_subcommands[0] == "n"):
                print("Command n entered")

                try:
                    ag_more_info = ""
                    with open('server.json') as serverfile:

                        server_data = load(serverfile)
                        read_n = default_display + int(ag_subcommands[1])
                        if (read_n > numelements):
                            read_n = numelements

                        for i in range(default_display, read_n):
                            ag_more_info += server_data['discussion_groups'][i]['groupname']
                            ag_more_info += '\n'
                    connectionSocket.sendall(ag_more_info.encode('UTF-8'))
                except IndexError:
                    connectionSocket.sendall("Please enter a number for subcommand 'n' ")
                    continue

            elif (ag_subcommands[0] == "q"):
                print("Command q entered")
                print("Closed out of ag command")
                connectionSocket.sendall("AG 500 CLOSE")
                break
            else:
                break

    elif (commands[0] == "sg"):
        print('Entered command state of sg')
        display = 5
        try:
            if not commands[1]:
                display = commands[1]
        except IndexError:
            pass

        ag_info = ""
        with open('server.json') as serverfile:
            server_data = load(serverfile)
            for i in range(numelements):
                ag_info += server_data['discussion_groups'][i]['groupname']
                ag_info += '\n'
        connectionSocket.sendall(ag_info.encode('UTF-8'))

        while (sg_flag == 0):
            sg_data = connectionSocket.recv(128)
            sg_subcommands = ag_data.split(" ")

            if (sg_subcommands[0] == "q"):
                print("Command q entered")
                print("Closed out of sg command")
                connectionSocket.sendall("SG 500 CLOSE")
                break


    elif (commands[0] == "rg"):
        print('Entered command state of rg')

        gname = ""
        display = 5
        try:
            if commands[1] != "":
                gname = commands[1]
        except IndexError:
            pass
        try:
            if commands[2] != "":
                display = commands[2]
        except IndexError:
            connectionSocket.send("Require a mandatory argument 'gname'\n")
            break
        
        postnum = 0
        rg_info = ""
        with open('server.json') as serverfile:
            server_data = load(serverfile)
            for i in range(numelements):
                if (server_data['discussion_groups'][i]['groupname'] == gname):
                    for j in server_data['discussion_groups'][i]['posts']:
                        postnum += 1
                    for k in range(postnum):
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['postid']
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['subject_line']
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['content_body']
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['author_id']
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['timestamp']
                        rg_info += '\n'


        connectionSocket.sendall(rg_info.encode('UTF-8'))

        while (rg_flag == 0):
            rg_data = connectionSocket.recv(128)
            rg_subcommands = ag_data.split(" ")

            if (rg_subcommands[0] == "q"):
                print("Command q entered")
                print("Closed out of rg command")
                connectionSocket.sendall("RG 500 CLOSE")
                break

                        
    elif (commands[0] == "logout"):
        connectionSocket.send('Closing your connection\r\n')
        print('Closing connection to client\n')
        connectionSocket.close()    
    else:
        connectionSocket.sendall("400 Bad Request\r\n")  

           
serverSocket.close()            
    
    

