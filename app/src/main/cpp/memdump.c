//
// Adapted by Irvin Homem. Originally written on StackOverflow by Tal Aroni
//

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <sys/ptrace.h>
#include <sys/socket.h>
#include <arpa/inet.h>

void dump_memory_region(FILE* pMemFile, unsigned long start_address, long length, int serverSocket)
{
    unsigned long address;
    int pageLength = 4096;
    unsigned char page[pageLength];
    fseeko(pMemFile, start_address, SEEK_SET);

    for (address=start_address; address < start_address + length; address += pageLength)
    {
        fread(&page, 1, pageLength, pMemFile);
        if (serverSocket == -1)
        {
            // write to stdout
            fwrite(&page, 1, pageLength, stdout);
        }
        else
        {
            send(serverSocket, &page, pageLength, 0);
        }
    }
}

//int main(int argc, char **argv) {
void main(int argc, char **argv) {

    if (argc == 2 || argc == 4)
    {
        int pid = atoi(argv[1]);
        long ptraceResult = ptrace(PTRACE_ATTACH, pid, NULL, NULL);
        if (ptraceResult < 0)
        {
            printf("Unable to attach to the pid specified\n");
            return;
        }
        wait(NULL);

        char mapsFilename[1024];
        sprintf(mapsFilename, "/proc/%s/maps", argv[1]);
        FILE* pMapsFile = fopen(mapsFilename, "r");
        char memFilename[1024];
        sprintf(memFilename, "/proc/%s/mem", argv[1]);
        FILE* pMemFile = fopen(memFilename, "r");
        int serverSocket = -1;
        if (argc == 4)
        {
            unsigned int port;
            int count = sscanf(argv[3], "%d", &port);
            if (count == 0)
            {
                printf("Invalid port specified\n");
                return;
            }
            serverSocket = socket(AF_INET, SOCK_STREAM, 0);
            if (serverSocket == -1)
            {
                printf("Could not create socket\n");
                return;
            }
            struct sockaddr_in serverSocketAddress;
            serverSocketAddress.sin_addr.s_addr = inet_addr(argv[2]);
            serverSocketAddress.sin_family = AF_INET;
            serverSocketAddress.sin_port = htons(port);
            if (connect(serverSocket, (struct sockaddr *) &serverSocketAddress, sizeof(serverSocketAddress)) < 0)
            {
                printf("Could not connect to server\n");
                return;
            }
        }
        char line[256];
        while (fgets(line, 256, pMapsFile) != NULL)
        {
            unsigned long start_address;
            unsigned long end_address;
            sscanf(line, "%08lx-%08lx\n", &start_address, &end_address);
            dump_memory_region(pMemFile, start_address, end_address - start_address, serverSocket);
        }
        fclose(pMapsFile);
        fclose(pMemFile);
        if (serverSocket != -1)
        {
            close(serverSocket);
        }

        ptrace(PTRACE_CONT, pid, NULL, NULL);
        ptrace(PTRACE_DETACH, pid, NULL, NULL);
    }
    else
    {
        printf("%s <pid>\n", argv[0]);
        printf("%s <pid> <ip-address> <port>\n", argv[0]);
        exit(0);
    }
}


