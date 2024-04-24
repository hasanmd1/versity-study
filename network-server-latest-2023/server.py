import socket
import sys

import threading
from asyncio import threads

from self import *
from time import sleep
from tcp_server import *


def main():
    '''declaring a new server using tcp_Server() class'''
    my_server = tcp_Server()

    '''declaring socket details'''
    mysock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    '''declaring server address and port number'''
    server_address = ('192.168.56.1', 6262)

    '''Just to print initialization our connection details'''
    print('starting up on {} port {}'.format(*server_address))

    '''binding the address, 192.168.56.1 to the socket, port 6262'''
    mysock.bind(server_address)

    '''Listen for new connection'''
    mysock.listen(10)

    '''establishing communication with the server 
        using function from tcp_Server class'''
    my_server.Communication_With_Server(mysock)

'''We are calling start_connection 
    which will start our connection using 
    server address using port 6262 and bind it to our new socket and 
    listen to incoming connections'''
if __name__ == "__main__":
    main()
