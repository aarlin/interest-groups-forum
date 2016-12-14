import sys, time
from socket import *
from pprint import *

argv = sys.argv
host = argv[1]
port = int(argv[2])

for i in range(3, 3):
	print("xd")

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((host,port))

sentence1 = ""
while (sentence1 != "logout"):
	sentence1 = input('Enter command: ')
	clientSocket.sendall(sentence1.encode('ascii'))
	rtn = clientSocket.recv(512).decode('ascii')
	pprint(rtn)

	if (rtn != "BAD COMMAND"):
		sub1 = ""
		while (sub1 != "q"):
			sub1 = input('Enter subcommands: ')
			clientSocket.sendall(sub1.encode('ascii'))
			rtn = clientSocket.recv(512)
			pprint(rtn.decode('ascii'))
			# if (rtn == "AG 500 CLOSE"):
			# 	break
		continue






# print(rtn.decode('UTF-8'))
# print("OUT OF INFINITY")
# clientSocket.sendall(sentence2.encode('utf-8'))
# modifiedSentence = clientSocket.recv(1024)
# print(modifiedSentence.decode('UTF-8'))
# clientSocket.close()
