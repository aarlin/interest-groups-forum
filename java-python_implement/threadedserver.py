# SERVER SIDE OF CSE310 PROJECT

# PROBLEMS WITH BADLY FORMATTED COMMANDS

from socket import *
from time import *
from threading import Thread
from SocketServer import ThreadingMixIn
from json import *
from pprint import *
from re import *
from StringIO import *
import os

class ClientThread(Thread):
            
    def __init__(self, ip, port, connectionSocket):             # INITIALIZE THE THREAD WITH IP, PORT AND CONNECTION SOCKET
        Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.connectionSocket = connectionSocket

    def run(self):
        while True:
            print('Waiting for user command\n')
            
            received_data = self.connectionSocket.makefile("r", 0).readline()       # READ COMMAND FROM USER
            receivebuffer = StringIO(256)
            receivebuffer.write(received_data)
            commands = receivebuffer.getvalue().splitlines()[0].split(" ")          # SPLIT THE COMMAND BETWEEN SPACES FOR PARSING
            receivebuffer.close()
            
            print("Client input:")
            print(commands)                                 # PRINT OUT WHAT THE CLIENT ENTERED ON THE SERVER TERMINAL
            
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
                        ag_info += '&'                                                  # DELIMITER FOR THE CLIENT TO PARSE
                    ag_info += '\n'
                self.connectionSocket.sendall(ag_info.encode('ascii'))                  # SEND OVER THE GROUP NAMES TO CLIENT

                while (ag_flag == 0):                                                   # ENTERED SUBCOMMANDS FOR ag
                    print('Entered subcommands state of ag')                
                    ag_data = self.connectionSocket.makefile("r", 0).readline()         # RECEIVE FROM CLIENT AGAIN
                    ag_buffer = StringIO(256)
                    ag_buffer.write(ag_data)
                    ag_subcommands = ag_buffer.getvalue().splitlines()[0].split(" ")
                    ag_buffer.close()

                    if (ag_subcommands[0] == "n"):                                      # CLIENT REQUESTED n MORE GROUPS TO BE SENT
                        print("Command n entered")

                        if (default_display == numelements):                            # WE HIT ALL THE GROUPS TO SEND
                            print("Printed all the groups available")
                            self.connectionSocket.sendall("AG 500 CLOSE\n")             # SEND SIGNAL TO KILL BREAK ag COMMAND STATE
                            break

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
                                    ag_more_info += '&'
                                ag_more_info += '\n'
                                default_display += inc_n                                # INCREMENT THE LAST GROUP COUNTER SENT POINTER
                                inc_n = 0                                               
                            self.connectionSocket.sendall(ag_more_info.encode('ascii'))      # SEND ALL N GROUPS REQUESTED
                        except IndexError:
                            self.connectionSocket.sendall("Please enter a number for subcommand 'n' \n")
                            continue

                    elif (ag_subcommands[0] == "q"):                                    # CLIENT REQEUESTED TO BREAK OUT OF ag COMMAND STATE
                        print("Command q entered")
                        print("Closed out of ag command")           
                        self.connectionSocket.sendall("AG 500 CLOSE\n")                        # SEND SIGNAL TO CLIENT TO GET OUT ag COMMAND STATE IN THEIR CODE
                        ag_flag == 1
                        break

            elif (commands[0] == "sg"):                                                 # IF CLIENT COMMAND IS sg
                print('Entered command state of sg')
                default_display = numelements                                           # WE WILL SEND ALL GROUPS, CLIENT KNOWS WHICH THEY ARE SUBSCRIBE TO
                postnum = 0

                ag_info = ""                                                                
                with open('server.json') as serverfile:                                 # OPEN JSON FILE TO GRAB ALL THE GROUPS
                    server_data = load(serverfile)
                    for i in range(numelements):
                        ag_info += server_data['discussion_groups'][i]['groupname']     # CONCAT GROUP NAME
                        for j in server_data['discussion_groups'][i]['posts']:                              # COUNT NUMBER OF POSTS IN THAT GROUP
                            postnum += 1
                        ag_info += '*'                                                  # SPLIT EACH GROUP NAME WITH '*' FOR THE CLIENT TO PARSE EASILY
                        ag_info += str(postnum)                                         # ADD THE POST NUMBER WITH EACH GROUP NAME
                        ag_info += '&'                                                  # INDICATE WHEN WE ARE DONE SENDING TO THE CLIENT
                        postnum = 0
                    ag_info += '\n' 
                self.connectionSocket.sendall(ag_info.encode('ascii'))

                while (sg_flag == 0):                                                   # ENTERED sg SUBCOMMAND STATE
                    sg_data = self.connectionSocket.makefile("r", 0).readline()         # RECEIVE FROM CLIENT AGAIN
                    sg_buffer = StringIO(256)
                    sg_buffer.write(sg_data)
                    sg_subcommands = sg_buffer.getvalue().splitlines()[0].split(" ")
                    sg_buffer.close()

                    if (sg_subcommands[0] == "q"):                                      # IF CLIENT ENTERS q THEN EXIT OUT OF sg COMMAND STATE
                        print("Command q entered")
                        print("Closed out of sg command")                   
                        self.connectionSocket.sendall("SG 500 CLOSE\n")                      
                        break


            elif (commands[0] == "rg"):                                                 # IF CLIENT COMMAND IS rg
                print('Entered command state of rg')

                gname = ""
                default_display = 1
                try:
                    if commands[1] != "":
                        gname = commands[1]
                except IndexError:
                    self.connectionSocket.send("Require a mandatory argument 'gname'\n")
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
                            if (default_display > postnum):                                                     # CHANGE DEFAULT DISPLAY TO MAX POSTS IF GREATER
                                default_display = postnum
                            else:   
                                default_display = postnum                                                       # ALWAYS GIVE THE CLIENT ALL THE POSTS
                            for k in range(default_display):                                                    # FOR NUMBER SPECIFIED BY CLIENT
                                inc_n += 1
                                rg_info += server_data['discussion_groups'][i]['posts'][k]['postid']            # GRAB POSTID
                                rg_info += '$'                                                                  # SEPERATE BY '$' FOR EASY PARSING BY THE CLIENT
                                rg_info += server_data['discussion_groups'][i]['posts'][k]['subject_line']      # GRAB SUBJECT LINE
                                rg_info += '$'
                                rg_info += server_data['discussion_groups'][i]['posts'][k]['content_body']      # GRAB CONTENT BODY
                                rg_info += '$'
                                rg_info += server_data['discussion_groups'][i]['posts'][k]['author_id']         # GRAB AUTHOR ID
                                rg_info += '$'
                                rg_info += server_data['discussion_groups'][i]['posts'][k]['timestamp']         # GRAB TIMESTAMP
                                rg_info += '&'
                            rg_info += '\n'
                self.connectionSocket.sendall(rg_info.encode('ascii'))                                       # SEND NUMBER OF POSTS REQUESTED

                while (rg_flag == 0):                                                                   # ENTER rg SUBCOMMAND STATE
                    inc_n = 0
                    rg_data = self.connectionSocket.makefile("r", 0).readline()                                # RECEIVE FROM CLIENT AGAIN
                    rg_buffer = StringIO(256)
                    rg_buffer.write(rg_data)
                    rg_subcommands = rg_buffer.getvalue().splitlines()[0].split(" ")
                    rg_buffer.close()

                    if (rg_subcommands[0] == "p"):
                        print("Command p entered")
                        rg_data = self.connectionSocket.makefile("r", 0).readline()                                # RECEIVE FROM CLIENT AGAIN
                        rg_buffer = StringIO(256)
                        rg_buffer.write(rg_data)
                        rg_post_data = rg_buffer.getvalue().splitlines()[0].split("$")                              # SPLIT BY THE '$' TO INDICATE SEPERATION OF DATA
                        rg_buffer.close()
                        
                        rg_post = {"postid" : rg_post_data[0],                                                    # CONSTRUCT A JSON POST FROM THE CLIENT'S POST DATA
                                   "subject_line" : rg_post_data[1],
                                   "content_body" : rg_post_data[2],
                                   "author_id" : rg_post_data[3],
                                   "timestamp" : rg_post_data[4]
                                    }
                        
                        with open('server.json') as serverfile:                                     # OPEN JSON FILE
                            server_data = load(serverfile)
                            for i in range(numelements):
                                if (server_data['discussion_groups'][i]['groupname'] == gname):     # FIND WHERE CLIENT'S GROUP NAME ENTERED FROM rg COMMAND
                                    server_data['discussion_groups'][i]['posts'].append(rg_post)    # ADD NEW POST TO THAT GROUP
                        print('Added post to discussion group ' + gname)
                        os.remove('server.json')                                                    # REMOVE THE JSON FILE
                        with open('server.json', 'w') as serverfile:                                # CREATE A NEW JSON FILE WITH POST ADDED
                            serverfile.write(dumps(server_data))
                        print('User entered a new post')
                        
                    elif (rg_subcommands[0] == "q"):                                                # EXIT OUT OF rg SUBCOMMAND STATE
                        print("Command q entered")
                        print("Closed out of rg command")
                        self.connectionSocket.sendall("RG 500 CLOSE\n")                             # IF CLIENT ENTERS q THEN EXIT OUT OF rg COMMAND STATE
                        break

                                    
            elif (commands[0] == "logout"):                                                         # ADDED LOGOUT COMMAND IF IN COMMAND STATE
                self.connectionSocket.send('Closing your connection\n')
                print('Closing connection to client\n')
                self.connectionSocket.close()    
            else:
                self.connectionSocket.sendall("BAD COMMAND\n")                                      # CLIENT SENT BAD COMMAND, ASK FOR ANOTHER
                continue
        #serverSocket.close()
    
    


# SERVER IS STARTED AND WAITS AT A KNOWN PORT FOR REQUESTS FROM CLIENTS
serverPort = 12001                          # HARD CODE SERVER PORT
serverSocket = socket(AF_INET, SOCK_STREAM) # CREATING SOCKET AND BINDING IT TO PORT
serverSocket.bind(('', serverPort))         
threads = []                                # LIST OF THREADS

# CREATE FLAGS FOR SUBCOMMAND WHILE LOOP
# THEY WILL NEVER CHANGE
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
while True:
    serverSocket.listen(100)                                            # LISTEN TO 100 CLIENTS
    print('The server is setup and ready to receive from client\n')
    connectionSocket, (ip,port) = serverSocket.accept()                 # ACCEPT A CLIENT 
    nthread = ClientThread(ip,port, connectionSocket)                   # CREATE A THREAD FROM CLIENT ACCEPTED WITH OWN IP, PORT, AND SOCKET
    nthread.start()                                                     # START RUNNING THE THREAD
    threads.append(nthread)                                             # ADD THREAD TO LIST

for thread in threads:
    thread.join()

