# SERVER SIDE OF CSE310 PROJECT

# with open('server.json', 'rb') as f:
#     connectionSocket.sendall(f.read())

# PROBLEMS WITH N COUNTER NOT WORKING
# PROBLEMS WITH BADLY FORMATTED COMMANDS

from socket import *
from time import *
from threading import *
from json import *
from pprint import *
from re import *

# SERVER IS STARTED AND WAITS AT A KNOWN PORT FOR REQUESTS FROM CLIENTS
serverPort = 12000                          # HARD CODE SERVER PORT
serverSocket = socket(AF_INET, SOCK_STREAM) # CREATING SOCKET AND BINDING IT TO PORT
serverSocket.bind(('', serverPort))         
serverSocket.listen(100)                    # ONLY LISTEN TO 100 CLIENTS

# CREATE FLAGS FOR SUBCOMMAND WHILE LOOP
ag_flag = 0     
sg_flag = 0
rg_flag = 0

# COUNT THE NUMBER OF DISCUSSION GROUPS IN THE JSON FILE
numelements = 0
with open('server.json') as serverfile:
    server_data = load(serverfile)
    for n in server_data['discussion_groups']:
        numelements += 1



# ACCEPT NEW CLIENT TO SERVER
print('The server is setup and ready to receive from client\n')
connectionSocket, addr = serverSocket.accept()

# SERVER RUNS FOREVER
while True:
    print('Waiting for user command')

    received_data = connectionSocket.recv(128)  # RECEIVE COMMAND FROM CLIENT
    commands = received_data.split()            # SPLIT THE COMMANDS BY SPACE
    print("Client input:")                  
    print(commands)

    if (commands[0] == "ag"):                       # IF CLIENT COMMAND IS ag
        print('Entered command state of ag')    
        default_display = 5                         # DEFAULT VALUE OF NUMBER OF GROUPS TO DISPLAY
        inc_n = 0                                   # COUNTER TO ADD TO WHEN WE SEND OVER A NUMBER OF GROUPS
        try:
            if commands[1] != "":                   # CLIENT ENTERED A NUMBER OF GROUPS TO DISPLAY
                default_display = int(commands[1])
        except IndexError:
            pass

        ag_info = ""                
        with open('server.json') as serverfile:                                 # OPEN JSON FILE TO PARSE
            server_data = load(serverfile)                      
            for i in range(default_display):                                
                ag_info += server_data['discussion_groups'][i]['groupname']     # GRAB EVERY DISCUSSION GROUP NAME UP TO NUMBER DISPLAYED
                ag_info += '\n'         
        connectionSocket.sendall(ag_info.encode('UTF-8'))                       # SEND OVER THE GROUP NAMES TO CLIENT

        while (ag_flag == 0):                                                   # ENTERED SUBCOMMANDS FOR ag
            print('Entered subcommands state of ag')                
            ag_data = connectionSocket.recv(128)                                # RECEIVE FROM CLIENT AGAIN
            ag_subcommands = ag_data.split(" ")

            if (ag_subcommands[0] == "n"):                                      # CLIENT REQUESTED n MORE GROUPS TO BE SENT
                print("Command n entered")

                try:
                    ag_more_info = ""
                    with open('server.json') as serverfile:                     # OPEN UP JSON FILE AGAIN AND GRAB MORE ENTRIES

                        server_data = load(serverfile)
                        read_n = default_display + int(ag_subcommands[1])       # READ UP TO THIS VARIABLE
                        if (read_n > numelements):                              # IF VARIABLE TO READ UP TO IS GREATER THAN GROUPS, SET TO GROUPS
                            read_n = numelements                

                        for i in range(default_display, read_n):                                # READ ANOTHER N VALUES TO SEND TO CLIENT
                            inc_n += 1
                            ag_more_info += server_data['discussion_groups'][i]['groupname']    # CONCAT GROUP NAME
                            ag_more_info += '\n'
                        default_display += inc_n                                # INCREMENT THE LAST GROUP COUNTER SENT POINTER
                        inc_n = 0                                               
                    connectionSocket.sendall(ag_more_info.encode('UTF-8'))      # SEND ALL N GROUPS REQUESTED
                    if (default_display == numelements):                        # WE HIT ALL THE GROUPS TO SEND
                        print("Printed all the groups available")
                        connectionSocket.sendall("AG 500 CLOSE")                # SEND SIGNAL TO KILL BREAK ag COMMAND STATE
                        break
                except IndexError:
                    connectionSocket.sendall("Please enter a number for subcommand 'n' ")
                    continue

            elif (ag_subcommands[0] == "q"):                                    # CLIENT REQEUESTED TO BREAK OUT OF ag COMMAND STATE
                print("Command q entered")
                print("Closed out of ag command")           
                connectionSocket.sendall("AG 500 CLOSE")                        # SEND SIGNAL TO CLIENT TO GET OUT ag COMMAND STATE IN THEIR CODE
                ag_flag == 1
                break

    elif (commands[0] == "sg"):                                                 # IF CLIENT COMMAND IS sg
        print('Entered command state of sg')
        default_display = numelements                                           # WE WILL SEND ALL GROUPS, CLIENT KNOWS WHICH THEY ARE SUBSCRIBE TO

        ag_info = ""                                                                
        with open('server.json') as serverfile:                                 # OPEN JSON FILE TO GRAB ALL THE GROUPS
            server_data = load(serverfile)
            for i in range(numelements):
                ag_info += server_data['discussion_groups'][i]['groupname']     # CONCAT GROUP NAME
                ag_info += '\n'
        connectionSocket.sendall(ag_info.encode('UTF-8'))

        while (sg_flag == 0):                                                   # ENTERED sg SUBCOMMAND STATE
            sg_data = connectionSocket.recv(128)                    
            sg_subcommands = sg_data.split(" ")                                 

            if (sg_subcommands[0] == "q"):                                      # IF CLIENT ENTERS q THEN EXIT OUT OF sg COMMAND STATE
                print("Command q entered")
                print("Closed out of sg command")                   
                connectionSocket.sendall("SG 500 CLOSE")                        
                break


    elif (commands[0] == "rg"):                                                 # IF CLIENT COMMAND IS rg
        print('Entered command state of rg')

        gname = ""
        default_display = 1
        try:
            if commands[1] != "":
                gname = commands[1]
        except IndexError:
            connectionSocket.send("Require a mandatory argument 'gname'\n")
            pass

        try:
            if commands[2] != "":
                default_display = int(commands[2])
        except IndexError:
            pass
        
        postnum = 0
        inc_n = 0
        rg_info = ""
        with open('server.json') as serverfile:                                                         # OPEN UP JSON FILE
            server_data = load(serverfile)                  
            for i in range(numelements):                                                                # FOR ALL GROUPS
                if (server_data['discussion_groups'][i]['groupname'] == gname):                         # IF GROUP NAME = gname
                    for j in server_data['discussion_groups'][i]['posts']:                              # COUNT NUMBER OF POSTS IN THAT GROUP
                        postnum += 1
                    if (default_display > postnum):                                                     # CHANGE DEFAULT DISPLAY TO POSTNUM IF GREATER
                        default_display = postnum
                    for k in range(default_display):                                                    # FOR NUMBER SPECIFIED BY CLIENT
                        inc_n += 1
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['postid']            # GRAB POSTID
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['subject_line']      # GRAB SUBJECT LINE
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['content_body']      # GRAB CONTENT BODY
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['author_id']         # GRAB AUTHOR ID
                        rg_info += ','
                        rg_info += server_data['discussion_groups'][i]['posts'][k]['timestamp']         # GRAB TIMESTAMP
                        rg_info += '\n'
                    default_display += inc_n
                    inc_n = 0

        connectionSocket.sendall(rg_info.encode('UTF-8'))                                       # SEND NUMBER OF POSTS REQUESTED

        while (rg_flag == 0):                                                                   # ENTER rg SUBCOMMAND STATE
            rg_data = connectionSocket.recv(128)
            rg_subcommands = rg_data.split(" ")

            if (rg_subcommands[0] == "n"):                                                      # IF CLIENT ENTERS SUBCOMMAND n
                print("Command n entered")
                try:
                    rg_more_info = ""
                    with open('server.json') as serverfile:                                     # OPEN THE JSON FILE

                        server_data = load(serverfile)                                          # LOAD JSON FILE
                        read_n = default_display + int(rg_subcommands[1])                       # READ UP TILL THIS VARIABLE
                        if (read_n > postnum):                                                  # IF VARIABLE GREATER THAN POSTNUM OF THIS GROUP
                            read_n = postnum                                                    # SET VARIABLE TO NUMBER OF POSTS

                        for i in range(numelements):                                            # GO THROUGH ALL GROUPS
                            if (server_data['discussion_groups'][i]['groupname'] == gname):     # IF GROUP NAME = gname
                                for k in range(default_display, read_n):                        # READ NUMBER OF POSTS STARTING FROM WHERE WE LEFT OFF
                                    inc_n += 1
                                    rg_more_info += server_data['discussion_groups'][i]['posts'][k]['postid']       # GRAB POSTID
                                    rg_more_info += ','
                                    rg_more_info += server_data['discussion_groups'][i]['posts'][k]['subject_line'] # GRAB SUBJECT LINE
                                    rg_more_info += ','
                                    rg_more_info += server_data['discussion_groups'][i]['posts'][k]['content_body'] # GRAB CONTENT BODY
                                    rg_more_info += ','
                                    rg_more_info += server_data['discussion_groups'][i]['posts'][k]['author_id']    # GRAB AUTHOR ID
                                    rg_more_info += ','
                                    rg_more_info += server_data['discussion_groups'][i]['posts'][k]['timestamp']    # GRAB TIMESTAMP
                                    rg_more_info += '\n'
                                default_display += inc_n
                                inc_n = 0
                        connectionSocket.sendall(rg_more_info.encode('UTF-8'))  # SEND THE POSTS TO CLIENT
                        if (default_display == postnum):                        # WE HIT ALL THE GROUPS TO SEND
                            print("Printed all the groups available")
                            connectionSocket.sendall("RG 500 CLOSE")           # SEND SIGNAL TO KILL BREAK ag COMMAND STATE
                            break

                except IndexError:
                    connectionSocket.sendall("Please enter a number for subcommand 'n' ")
                    continue

            elif (rg_subcommands[0] == "q"):                    # EXIT OUT OF rg SUBCOMMAND STATE
                print("Command q entered")
                print("Closed out of rg command")
                connectionSocket.sendall("RG 500 CLOSE")        # SEND TO 
                break

                            
    elif (commands[0] == "logout"):                             # ADDED LOGOUT COMMAND IF IN COMMAND STATE
        connectionSocket.send('Closing your connection\r\n')
        print('Closing connection to client\n')
        connectionSocket.close()    
    else:
        connectionSocket.sendall("BAD COMMAND")                 # CLIENT SENT BAD COMMAND, ASK FOR ANOTHER
        continue

           
serverSocket.close()            
    
    

