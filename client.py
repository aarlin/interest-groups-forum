import sys, time
from socket import *

argv = sys.argv
host = argv[1]
port = int(argv[2])

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((host,port))
sentence1 = input('Enter command: ')
clientSocket.sendall(sentence1.encode('utf-8'))
while (clientSocket.recv(128) != "AG 200 OK"):
    sub1 = input('Enter subcommands: ')
    clientSocket.sendall(sub1.encode('utf-8'))
    rtn = clientSocket.recv(128)
    print(rtn.decode('UTF-8'))
print("OUT OF INFINITY")
clientSocket.sendall(sentence2.encode('utf-8'))
modifiedSentence = clientSocket.recv(1024)
print(modifiedSentence.decode('UTF-8'))
clientSocket.close()
