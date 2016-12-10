import sys, time
from socket import *
from pprint import *

argv = sys.argv
host = argv[1]
port = int(argv[2])

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((host,port))
sentence1 = input('Enter command: ')

clientSocket.sendall(sentence1.encode('ascii'))
rtn = clientSocket.recv(512)
pprint(rtn.decode('ascii'))

sub1 = input('Enter subcommands: ')
while (sub1 != "q"):
	clientSocket.sendall(sub1.encode('utf-8'))
	rtn = clientSocket.recv(512)
	pprint(rtn.decode('ascii'))
	sub1 = input('Enter subcommands: ')



# print(rtn.decode('UTF-8'))
# print("OUT OF INFINITY")
# clientSocket.sendall(sentence2.encode('utf-8'))
# modifiedSentence = clientSocket.recv(1024)
# print(modifiedSentence.decode('UTF-8'))
# clientSocket.close()
