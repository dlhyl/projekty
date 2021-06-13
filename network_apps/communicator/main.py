import socket
import crcmod as crc
import time
from threading import Thread, Lock
import threading
import os
import struct
import tkinter as tk
from tkinter import filedialog, ttk, scrolledtext, messagebox

######################################################### CONSTANTS #########################################################

HeaderSize = 9                              # Header size
KeepAliveHeaderSize = 3                     # Keep-alive Header size
MSS = 1463                                  # Maximum segment size

ACK = 1; NACK = 1 << 1; TEXT = 1 << 2       ########################
FILE = 1 << 3; CONN_INIT = 1 << 4           #        Flags         #
CONN_FIN = 1 << 5; KEEP_ALIVE = 1 << 6      ########################

windowSize = 4                              # Selective ARQ window size
crc16 = crc.predefined.mkPredefinedCrcFun("crc-16") # CRC-16 CCITT

#############################################################################################################################

class GUI:
    def __init__(self):
        root = tk.Tk()
        root.title('UDP Komunikator')
        width = 900; height = 500
        root.minsize(width, height)
        root.geometry('%dx%d'%(width, height))

        self.root = root
        self.frame = self.mainFrame(self)
        self.queue = []
        self.root.after(200,self.processIncoming)
        self.root.mainloop()

    def switchFrame(self, frame_class):
        new_frame = frame_class(self)
        if self.frame is not None:
            self.frame.destroy()
        self.frame = new_frame

    def mainMenu(self):
        self.switchFrame(self.mainFrame)
        
    class mainFrame(tk.Frame):
        def __init__(self, gui):
            tk.Frame.__init__(self, gui.root)
            self.pack(fill=tk.BOTH, expand=True)
            serverButton = ttk.Button(self, text="Server", command = lambda: gui.switchFrame(gui.serverFrame))
            clientButton = ttk.Button(self, text="Client", command = lambda: gui.switchFrame(gui.clientFrame))
            serverButton.place(relx=0.58, rely=0.5, anchor=tk.CENTER)
            clientButton.place(relx=0.42, rely=0.5, anchor=tk.CENTER)

    def processIncoming(self):
        while len(self.queue):
            try:
                msg, code = self.queue.pop(0)
                if code == 2:
                    self.frame.printTextMessage(msg)
                elif code == 3:
                    self.frame.printInfoMessage(msg[0],msg[1])
                else:
                    self.frame.printInfoMessage(msg)
            except:
                pass
        self.root.after(200,self.processIncoming)

    class clientFrame(tk.Frame):
        def __init__(self, gui):
            tk.Frame.__init__(self, gui.root)
            self.client = None
            # == Settings frame ==
            settingsLF = tk.LabelFrame(self, text='Nastavenia')
            settingsLF.grid(row=1, column=1, padx=15, pady=10, ipadx=10, ipady=10, sticky=("nsew"))
            for x in range(6):
                settingsLF.columnconfigure(x, weight=1);settingsLF.rowconfigure(x, weight=3)
            
            def connectClient():
                try:
                    port = int(portEntry.get())
                    if port < 1024 or port > 65535:
                        raise Exception()
                except:
                    messagebox.showwarning("Warning","Port musi byt 1024 - 65 535!")
                    return 
                portEntry.config(state=tk.DISABLED)
                ipEntry.config(state=tk.DISABLED)
                setButton.config(state=tk.DISABLED)
                self.client = Client(gui)
                self.client.start() 

            labelIPClient = tk.Label(settingsLF,text="IP Adresa Clienta"); labelPortClient = tk.Label(settingsLF,text="PORT Clienta")
            ipEntryClient = ttk.Entry(settingsLF,state=tk.DISABLED, width=25, cursor="arrow"); portEntryClient = ttk.Entry(settingsLF, state=tk.DISABLED,width=25, cursor="arrow")
            labelIP = tk.Label(settingsLF,text="IP Adresa Servera"); labelPort = tk.Label(settingsLF,text="PORT Servera")
            ipEntry = ttk.Entry(settingsLF,width=25); portEntry = ttk.Entry(settingsLF,width=25)
            setButton = ttk.Button(settingsLF, text="Spojit", command = connectClient)
            self.client_ip = ipEntryClient; self.client_port = portEntryClient; self.server_ip = ipEntry; self.server_port = portEntry

            labelIPClient.grid(row=0,column=0, padx=15, pady=(10,5), sticky=tk.W); ipEntryClient.grid(row=0,column=1, pady=(10,5), sticky=("nsew"))
            labelPortClient.grid(row=1,column=0, padx=15, pady=(0,5), sticky=tk.W); portEntryClient.grid(row=1,column=1, pady=(0,5), sticky=("nsew"))
            labelIP.grid(row=2,column=0, padx=15, pady=(10,5), sticky=tk.W); ipEntry.grid(row=2,column=1, pady=(10,5), sticky=("nsew"))
            labelPort.grid(row=3,column=0, padx=15, pady=(0,5), sticky=tk.W); portEntry.grid(row=3,column=1, pady=(0,5), sticky=("nsew"))
            setButton.grid(row=1,column=2, rowspan=2, columnspan=1, padx=(10,0))
            
            # == Information panel Frame ==
            infPanelLF = tk.LabelFrame(self, text='Informacny panel')
            infPanelLF.grid(row=2, column=2, padx=(0,15), pady=10, sticky=("nsew"))

            infPanel = scrolledtext.ScrolledText(infPanelLF, width = 50, height = 15, padx=5, pady=5, font=('Arial','9'))
            infPanel.tag_config('important',font=('Arial','9','bold'))
            infPanel.tag_config('warning', foreground="red", font=('Arial','9','bold'))
            infPanel.tag_config('success', foreground="green", font=('Arial','9','bold'))
            infPanel.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
            self.infoPanel = infPanel

            # == Sending files or messages Frame ==
            def chooseFile():
                file_selected = filedialog.askopenfilename()
                if file_selected:
                    self.filePath = file_selected
                    fileName.set("%s (%.1fkB)"%(os.path.basename(file_selected), os.stat(self.filePath).st_size/1000))

            def only_numeric_input(P):
                if (P.isdigit() and int(P) in range(1,MSS+1)) or P == "":
                    return True
                return False

            sendingLF = tk.LabelFrame(self, text='Odosielanie')
            sendingLF.grid(row=2, column=1, padx=15, pady=(5,10), sticky=("nsew"))
            for x in range(5):
                sendingLF.columnconfigure(x, weight=1);sendingLF.rowconfigure(x, weight=1)
            sendingLF.rowconfigure(5, weight=10)

            filePath = ""
            fileName = tk.StringVar(sendingLF, os.path.basename(filePath))
            def sendFile():
                try:
                    os.path.exists(self.filePath)
                except:
                    messagebox.showerror("Error","Prosim zvolte subor")
                    return
                if insertErrorButton.instate(['selected']):
                    self.client.sendFile(self.filePath, int(MSSEntry.get()), my_combobox.current())
                else:
                    self.client.sendFile(self.filePath, int(MSSEntry.get()))

            errFrame = tk.Frame(sendingLF)
            my_combobox = ttk.Combobox(errFrame, height=2,state="readonly", exportselection=False)
            my_combobox['values'] = ("Poskodeny 1. frag.", "Stratene ACK 1. frag")
            my_combobox.current(0)
            MSSLabel = tk.Label(sendingLF, text="Velkost fragmentu (1-%d)"%(MSS)); MSSEntry = ttk.Entry(sendingLF,font=("Arial 10"), width=20, validate="key", validatecommand=(sendingLF.register(only_numeric_input), "%P"));MSSEntry.insert(0,"1463")
            sendFileLabel = tk.Label(sendingLF, text="Odoslanie suboru"); fileEntry = ttk.Entry(sendingLF, width=20, state=tk.DISABLED, textvariable=fileName); browseButton = tk.Button(sendingLF, text="Vybrat..", command = chooseFile, font=('Helvetica','7')) 
            sendFileBtn = ttk.Button(errFrame, text="Odoslat subor", command=sendFile); insertErrorButton = ttk.Checkbutton(errFrame, text="Chybny fragment",takefocus=0);
            insertErrorButton.invoke(); insertErrorButton.invoke()
            MSSLabel.grid(row=1, column=1, sticky="w", padx=10, pady=(0,5)); MSSEntry.grid(row=1,column=2,sticky="wnes", padx=10, pady=(0,5))
            sendFileLabel.grid(row=2,column=1,sticky="w", padx=10, pady=(0,5)); fileEntry.grid(row=2,column=2,sticky="wnes", padx=10, pady=(0,5)); browseButton.grid(row=2,column=3,sticky="we", padx=(0,10))
            errFrame.grid(row=3, column=1, pady=(0,5), columnspan=3,padx=10, sticky=("wens")); insertErrorButton.pack(side=tk.LEFT, padx=(0,5)); my_combobox.pack(side=tk.LEFT, padx=(0,10)); sendFileBtn.pack(side=tk.RIGHT, padx=(0,10)); ttk.Separator(sendingLF, orient=tk.HORIZONTAL).grid(row=4, column=1, columnspan=3, sticky="wnes", pady=(5,10), padx=10)
            # my_combobox.grid(row=3, column=2,pady=(0,5)); insertErrorButton.grid(row=3,column=1,pady=(0,5)); sendFileBtn.grid(row=3,column=3,pady=(0,5))
            MSG = scrolledtext.ScrolledText(sendingLF, state=tk.DISABLED, width=50, height=10); 
            def sendTextMessage():
                txt=msgEntry.get()
                if txt == "":
                    return
                msgEntry.delete(0,tk.END)
                MSG.config(state=tk.NORMAL)
                MSG.insert(tk.END, txt + '\n')
                MSG.config(state=tk.DISABLED)
                if insertErrorButton.instate(['selected']):
                    self.client.sendTextMessage(txt,int(MSSEntry.get()), my_combobox.current())
                else:
                    self.client.sendTextMessage(txt,int(MSSEntry.get()))
            msgEntry = ttk.Entry(sendingLF, font=("Arial 9"))
            sendMessageBtn = ttk.Button(sendingLF, text="Send message", command=sendTextMessage)
            MSG.grid(row=5, column=1, columnspan=3, sticky="wens", padx=10, pady=(0,5)); msgEntry.grid(row=6, column=1, columnspan=2, sticky="wens", padx=(10,5),pady = (0,5)); sendMessageBtn.grid(row=6,column=3, sticky="e",padx=(0,10), pady=(0,5))

            # == Statistics and quit Frame ==
            comFrame = tk.Frame(self)
            comFrame.grid(row=1, column=2, padx=(0,15), pady=10, sticky=("nsew"))

            statsLF = tk.LabelFrame(comFrame, text="Statistika a informacie")
            statsLF.pack(side=tk.LEFT, anchor=tk.N, fill=tk.BOTH, expand=True)

            statusLabel = tk.Label(statsLF, text="Status"); statusAnswerLabel = tk.Label(statsLF, text="Offline", fg="red")
            reziaLabel = tk.Label(statsLF, text="Velkost rezie"); velkostRezieLabel = tk.Label(statsLF, text="0B")
            self.reziaLabel = velkostRezieLabel
            statusLabel.grid(row=1, column=1, padx=10, sticky=tk.W); statusAnswerLabel.grid(row=1, column=2, padx=10, sticky=("nsew"))
            reziaLabel.grid(row=2, column=1, padx=10, sticky=tk.W); velkostRezieLabel.grid(row=2, column=2, padx=10, sticky=("nsew"))
            
            self.status = statusAnswerLabel
            def changeRole():
                if self.client is not None:
                    self.client.disconnectConnection()
                    gui.mainMenu()
                else:
                    gui.mainMenu()
            backButton = ttk.Button(comFrame, text="Zmenit rolu", command = changeRole)
            backButton.pack(side=tk.RIGHT, anchor=tk.N, padx=10, pady=10)
                
            self.columnconfigure(1, weight=1);self.columnconfigure(2, weight=3)
            self.rowconfigure(1, weight=1);self.rowconfigure(2, weight=3)
            self.pack(fill=tk.BOTH, expand=True)

        def getAddress(self):
            return (self.getServerIP, self.getServerPort)

        def changeStatus(self, code):
            statusCodes = {0:"Client Offline", 1:"Client Online", 2: "Client connected to server"}
            colors = ["red","orange","green"]
            self.status['text']=statusCodes[code]
            self.status['fg']=colors[code]

        def printInfoMessage(self, message, tag=None):
            self.infoPanel.config(state=tk.NORMAL)
            if tag is not None:
                self.infoPanel.insert(tk.END, message + '\n', tag)
            else:
                self.infoPanel.insert(tk.END, message + '\n')
            self.infoPanel.config(state=tk.DISABLED)

        def setClientIP(self, ip):
            self.client_ip.config(state=tk.NORMAL)
            self.client_ip.delete(0,tk.END)
            self.client_ip.insert(0,ip)
            self.client_ip.config(state=tk.DISABLED)

        def setClientPort(self, port):
            self.client_port.config(state=tk.NORMAL)
            self.client_port.delete(0,tk.END)
            self.client_port.insert(0,str(port))
            self.client_port.config(state=tk.DISABLED)

        def getServerIP(self):
            return self.server_ip.get()
        
        def getServerPort(self):
            return int(self.server_port.get())

    class serverFrame(tk.Frame):
        def __init__(self, gui):
            tk.Frame.__init__(self, gui.root)
            self.server = None
            # == Settings frame ==
            def chooseDirectory():
                folder_selected = filedialog.askdirectory()
                if folder_selected:
                    directory.set(folder_selected)

            def connectServer():
                try:
                    port = int(portEntry.get())
                    if port < 1024 or port > 65535:
                        raise Exception()
                except:
                    messagebox.showwarning("Warning","Port must be 1024 - 65 535!")
                    return 
                portEntry.config(state=tk.DISABLED);ipEntry.config(state=tk.DISABLED)
                self.server = Server(gui)
                self.server.start()
                setButton.config(state=tk.DISABLED)

            settingsLF = tk.LabelFrame(self, text='Nastavenia')
            settingsLF.grid(row=1, column=1, padx=15, pady=10, ipadx=10, ipady=10, sticky=("nsew"))
            for x in range(3):
                settingsLF.columnconfigure(x, weight=1);settingsLF.rowconfigure(x, weight=3)

            directory = tk.StringVar(settingsLF, value=os.path.dirname(os.path.abspath(__file__))); self.directory = directory
            labelIP = tk.Label(settingsLF,text="Vasa IP"); labelPort = tk.Label(settingsLF,text="Vas PORT"); labelDir = tk.Label(settingsLF,text="Miesto ulozenia")
            ipEntry = ttk.Entry(settingsLF,state=tk.DISABLED, width=20,font=("Arial 10")); portEntry = ttk.Entry(settingsLF,width=20, font=("Arial 10")); dirEntry = ttk.Entry(settingsLF, width=20, state=tk.DISABLED, textvariable=directory); 
            setButton = ttk.Button(settingsLF, text="Spojit", command = connectServer); browseButton = tk.Button(settingsLF, text="Zvolit..", command = chooseDirectory, font=('Helvetica','7')) 
            self.serverIP = ipEntry; self.serverPort = portEntry

            labelIP.grid(row=0,column=0, padx=10, pady=(0,5), sticky=("w")); ipEntry.grid(row=0,column=1, pady=(0,5), sticky=("nsew"))
            labelPort.grid(row=1,column=0, padx=10, pady=(0,5), sticky=("w")); portEntry.grid(row=1,column=1, pady=(0,5), sticky=("nsew"))
            setButton.grid(row=0, column=2, rowspan=2, padx=15, sticky=("ew"))
            labelDir.grid(row=2, column=0, padx=10, pady=(0,10), sticky=("w")); dirEntry.grid(row=2, column=1, pady=(0,10), sticky=("nsew")); browseButton.grid(row=2, column=2, padx=20, pady=(0,10), sticky=("ew"))

            # == Information panel Frame ==
            infPanelLF = tk.LabelFrame(self, text='Informacny panel')
            infPanelLF.grid(row=2, column=2, padx=(0,15), pady=10, sticky=("nsew"))

            infPanel = scrolledtext.ScrolledText(infPanelLF, width = 50, height = 15, padx=5, pady=5, font=('Arial','9'))
            infPanel.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
            infPanel.tag_config('important', font=('Arial','9','bold'))
            infPanel.tag_config('warning', foreground="red", font=('Arial','9','bold'))
            infPanel.tag_config('success', foreground="green", font=('Arial','9','bold'))
            self.infoPanel = infPanel

            # == Messages Frame ==

            msgsLF = tk.LabelFrame(self, text='Prichadzajuce spravy')
            msgsLF.grid(row=2, column=1, padx=15, pady=(10,5), ipadx=10, ipady=10, sticky=("nsew"))

            msgPanel = scrolledtext.ScrolledText(msgsLF,state=tk.DISABLED, width = 50, height = 15, padx=5, pady=5, font=('Arial','9'))
            msgPanel.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
            self.msgPanel = msgPanel

            # == Statistics and quit Frame ==
            comFrame = tk.Frame(self)
            comFrame.grid(row=1, column=2, padx=(0,15), pady=(10,5), sticky=tk.W+tk.E+tk.N+tk.S)

            statsLF = tk.LabelFrame(comFrame, text="Statistika a informacie")
            statsLF.pack(side=tk.LEFT, anchor=tk.N, fill=tk.BOTH, expand=True)

            statusLabel = tk.Label(statsLF, text="Status"); statusAnswerLabel = tk.Label(statsLF, text="Offline", fg="red")
            statusLabel.grid(row=1, column=1, padx=10, sticky=tk.W); statusAnswerLabel.grid(row=1, column=2, padx=10, sticky=tk.E)
            self.status = statusAnswerLabel

            def changeRole():
                if self.server is not None:
                    self.server.disconnectConnection()
                    gui.mainMenu()
                else:
                    gui.mainMenu()
            backButton = ttk.Button(comFrame, text="Zmenit rolu", command = changeRole)
            backButton.pack(side=tk.RIGHT, anchor=tk.N, padx=10, pady=10)

            self.columnconfigure(1, weight=1);self.columnconfigure(2, weight=3)
            self.rowconfigure(1, weight=1);self.rowconfigure(2, weight=3)
            self.pack(fill=tk.BOTH, expand=True)

        def getAddress(self):
            return (self.getServerIP, self.getServerPort)

        def changeStatus(self, code):
            statusCodes = {0:"Offline", 1: "Server listening", 2: "Server connected to client"}
            colors = ["red","orange","green"]
            self.status['text']=statusCodes[code]
            self.status['fg']=colors[code]

        def printTextMessage(self, message):
            self.msgPanel.config(state=tk.NORMAL)
            self.msgPanel.insert(tk.END, message + '\n')
            self.msgPanel.config(state=tk.DISABLED)

        def printInfoMessage(self, message, tag=None):
            self.infoPanel.config(state=tk.NORMAL)
            if tag is not None:
                self.infoPanel.insert(tk.END, message + '\n', tag)
            else:
                self.infoPanel.insert(tk.END, message + '\n')
            self.infoPanel.config(state=tk.DISABLED)

        def setServerIP(self, ip):
            self.serverIP.config(state=tk.NORMAL)
            self.serverIP.delete(0,tk.END)
            self.serverIP.insert(0,ip)
            self.serverIP.config(state=tk.DISABLED)

        def getServerIP(self):
            return self.serverIP.get()
        
        def getServerPort(self):
            return int(self.serverPort.get())

def createHeader(crc,flags,length,seqNumber):
    return struct.pack('!HBHI',crc,flags,length,seqNumber)
    
def createKeepAliveHeader(crc, flags):
    return struct.pack('!HB',crc, flags)

def decodeHeader(header):
    return struct.unpack('!HBHI', header)

def decodeKeepAliveHeader(header):
    return struct.unpack('!HB', header)

def calcCRC(data):
    return crc16(data)

def createFlags(*args):
    res = 0
    for flag in args:
        res |= flag
    return res

def isKeepAlive(flags):
    return flags & KEEP_ALIVE

def isFile(flags):
    return flags & FILE

def isTextMessage(flags):
    return flags & TEXT

def isInitial(flags):
    return flags & CONN_INIT

def isFinal(flags):
    return flags & CONN_FIN

def isACK(flags):
    return flags & ACK

def isNACK(flags):
    return flags & NACK

""" Server thread """
class Server(Thread):
    def __init__(self, GUI = None):
        Thread.__init__(self, name="server", daemon=True)
        self.frame = GUI.frame
        self.s = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)  
        ip = socket.gethostbyname_ex(socket.gethostname())[2][-1]
        port = GUI.frame.getServerPort()
        self.s.bind(("",port))
        self.clientAddr = None
        self.gui = GUI
        self.q = GUI.queue
        self.serverOnline = True
        self.keepAliveExpectedCount = 0
        GUI.frame.setServerIP(ip)
        GUI.frame.printInfoMessage("Server listening on "+ip+":"+str(port),'important')
        GUI.frame.changeStatus(1)

    """ Prints to gui Infopanel or Text Message Panel """
    def guiPrint(self, text, code):
        self.q.append((text,code))

    """ Disconnect function for server. Sends CONN_FIN packet to client and return to GUI. """
    def disconnectConnection(self):
        if self.clientAddr is not None:
            try:
                self.s.sendto(createHeader(0,CONN_FIN,0,0),self.clientAddr)
            except:
                pass
        self.serverOnline = False
        self.s.close()

    """ Main function of server - receives packets """
    def run(self):
        while self.serverOnline:
            try:
                rawData,addr = self.s.recvfrom(MSS+HeaderSize)
            except socket.timeout:                                  # Keep alive timeout for server, 1st Keep Alive Time out = 25s, 2nd Keep Alive Time out = 15s, Disconnect after 25+15 seconds = 40 secs
                self.keepAliveExpectedCount+=1
                self.guiPrint("Expected keep alive #%d not received [%2.0fs without receiving any data]"%(self.keepAliveExpectedCount, self.s.gettimeout()),1)
                if self.keepAliveExpectedCount == 2:
                    messagebox.showwarning("Client disconnected","Client sa odpojil.")
                    self.gui.switchFrame(self.gui.serverFrame)
                    return
                if self.keepAliveExpectedCount == 1:
                    self.s.settimeout(15)
                continue
            except:
                continue

            if isKeepAlive(rawData[2]):     # Process keep alive message
                self.guiPrint("Keep Alive #%d received."%(int(rawData[1])),1)
                if self.keepAliveExpectedCount > 0:
                    self.keepAliveExpectedCount-=1
                self.s.sendto(createKeepAliveHeader(int(rawData[1]),KEEP_ALIVE | ACK), addr)
            else:                           # Process packet
                header, data = rawData[:HeaderSize],rawData[HeaderSize:]
                crc,flags,length,seqNumber = decodeHeader(header)      

                if isTextMessage(flags) or isFile(flags):       # FILE or TEXT MESSAGE
                    if isInitial(flags):
                        startSendTime = time.time()
                        fragmentSize = length
                        fragmentCount = seqNumber

                        if isFile(flags):                       # Initial packet for FILE TRANSFER
                            fileName = str(data,"utf-8")
                            self.guiPrint(["File '%s' will be received (MSS %dB, %d fragments)"%(fileName, fragmentSize, fragmentCount),'important'],3)
                            loc=self.frame.directory.get()
                            if not os.path.exists(loc+fileName):
                                open(loc+'/'+fileName, 'w').close()
                            f = open(loc+'/'+fileName, "ab+")
                        else:                                   # Initial packet for TEXT MESSAGE
                            self.guiPrint(["Text Message will be received (MSS %dB, %d fragments)"%(fragmentSize, fragmentCount),'important'],3)
                            message = ""
                        window = {}
                        expected_seqN = 0
                        flags |= ACK
                        self.s.sendto(createHeader(crc,flags,0,0), addr)

                    elif isFinal(flags):
                        flags |= ACK
                        self.s.sendto(createHeader(crc,flags,length, seqNumber), addr)
                        if isFile(flags):                        # Last packet for FILE TRANSFER
                            f.close()
                            self.guiPrint(["File %s received [%s]"%(fileName, loc+'/'+fileName),'success'],3)
                        else:                                    # Last packet for TEXT MESSAGE
                            self.guiPrint(["Text Message received",'success'],3)
                            self.guiPrint(message,2)
                        self.guiPrint("Transfer took %.3fsec"%(time.time()-startSendTime),1)
                        

                    else:                                        # RECEIVING OF DATA FRAGMENTS - USING SELECTIVE ARQ
                        if seqNumber < expected_seqN:
                            self.guiPrint([" >> Received already accepted fragment #%d/%d (%dB Data + %dB Header)" %(seqNumber+1, fragmentCount, length, HeaderSize),'important'],3)
                            self.guiPrint(" << Sending ACK for fragment #%d/%d" %(seqNumber+1, fragmentCount),1)
                            self.s.sendto(createHeader(0, flags | ACK, 0, seqNumber), addr)

                        elif seqNumber < expected_seqN + windowSize:
                            if crc16(data) != crc or len(data) != length:
                                self.guiPrint([" >> Received corrupt fragment #%d/%d, sending NACK.."%(seqNumber+1, fragmentCount),'warning'],3)
                                self.s.sendto(createHeader(0, flags | NACK, 0, seqNumber), addr)     # send NACK if crc doesnt match
                            
                            else:
                                self.guiPrint(" >> Received fragment #%d/%d (%dB Data + %dB Header)" %(seqNumber+1, fragmentCount, length, HeaderSize),1)
                                if expected_seqN == seqNumber:
                                    if isFile(flags):
                                        f.write(data[:length])
                                    else:
                                        message+=str(data[:length], 'utf-8')
                                    expected_seqN+=1
                                    while expected_seqN in window.keys():
                                        if isFile(flags):
                                            f.write(window.pop(expected_seqN))
                                        else:
                                            message+=str(window.pop(expected_seqN), 'utf-8')
                                        expected_seqN+=1
                                    self.s.sendto(createHeader(crc,flags | ACK,length,seqNumber),addr)
                                else:
                                    window[seqNumber]=data[:length]
                                    self.s.sendto(createHeader(crc,createFlags(flags | ACK),length,seqNumber),addr)
                                self.guiPrint(" << Sending ACK for fragment #%d/%d" %(seqNumber+1, fragmentCount),1)
      
                elif isInitial(flags):                              # Connection initialization
                    self.guiPrint(["Client (%s:%d) connected."%(addr[0],addr[1]),'important'],3)
                    flags |= ACK
                    self.clientAddr = addr
                    self.s.sendto(createHeader(crc,flags,length,seqNumber),addr)
                    self.frame.changeStatus(2)
                    self.s.settimeout(25)
                
                elif flags == CONN_FIN:                             # Disconnect handle
                    self.guiPrint(["Client disconnected from the server.",'important'],3)
                    self.s.settimeout(None)
                    self.keepAliveExpectedCount = 0
                    self.frame.changeStatus(1)
                    messagebox.showinfo("Disconnect", "Client sa dobrovolne odpojil.")

"""
Client (sender) thread.
"""
class Client(Thread):           
    def __init__(self, GUI = None):
        Thread.__init__(self, name="client", daemon=True)
        self.s = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
        self.gui = GUI
        self.frame = GUI.frame
        self.serverIP = GUI.frame.getServerIP()
        self.serverPort = GUI.frame.getServerPort()
        self.serverAddress = (self.serverIP, self.serverPort)
        self.q = GUI.queue
        self.threadLock = Lock()
        self.initializedConnection = False
        self.initializedDataTransfer = False
        self.keepAliveCounter = 0
        self.velkostRezie = 0
        GUI.frame.changeStatus(1)

    """ Print to GUI Info Panel. """
    def guiPrint(self, text, code):
        self.q.append((text,code))

    """ Client thread run function. Starts with initialization (sends packet every 3 seconds until connection is made).
        Start receiver thread for receiving packets from server. Also start keep alive thread for keeping the connection alive. """ 
    def run(self):
        receiverThread = Thread(target=self.receiver, name="receiver", daemon=True)
        stT = time.time()
        self.guiPrint("Trying to connect to %s:%d"%(self.serverAddress[0],self.serverAddress[1]),1)
        self.initiateConnection()
        self.frame.setClientIP(socket.gethostbyname_ex(socket.gethostname())[2][-1])
        self.frame.setClientPort(self.s.getsockname()[1])
        receiverThread.start()
        while not self.initializedConnection and self.keepAliveCounter < 3:
            if time.time() - stT > 3:
                self.guiPrint("Trying to connect to %s:%d"%(self.serverAddress[0],self.serverAddress[1]),1)
                self.initiateConnection()
                stT=time.time()

        keepAliveThread = Thread(target=self.keepAlive,daemon=True,name="keep alive")
        keepAliveThread.start()
        keepAliveThread.join()

    """ Tries to send packet to server socket, prints error if server socket is not bound. """
    def sendPacket(self, packet):
        try:
            self.s.sendto(packet, self.serverAddress)
            self.velkostRezie+=HeaderSize if not isKeepAlive(packet[2]) else KeepAliveHeaderSize
            self.frame.reziaLabel["text"]=str(self.velkostRezie)+"B"
        except:
            self.guiPrint(["Could not send packet to %s:%d"%(self.serverAddress[0],self.serverAddress[1])],1)

    """ Sends initial packet to server """
    def initiateConnection(self):
        self.sendPacket(createHeader(0, createFlags(CONN_INIT), 0, 0))

    """ Disconnects from Client. Sends CONN_FIN packet to server and stops Client thread. """
    def disconnectConnection(self):
        self.sendPacket(createHeader(0, createFlags(CONN_FIN), 0, 0))
        self.keepAliveCounter = 3

    """ Keep Alive Thread. Sends 1st Keep Alive after 20s of inactivity (no data transfer).
        Sends 2nd Keep Alive 15s after sending 1st Keep Alive if Client didn't get respond.
        Then waits 10s for response. Resets the Client Window and prints message to user if
        server didn't response in 45 seconds. 
    """
    def keepAlive(self):
        startTime = time.time()
        while self.keepAliveCounter < 3:
            if self.initializedDataTransfer:
                self.keepAliveCounter = 0
                time.sleep(1)
                startTime = time.time()
                continue

            if self.keepAliveCounter == 0 and time.time() - startTime > 20:
                self.guiPrint("Sending keep alive #1 [after 20s]",1)
                self.sendPacket(createKeepAliveHeader(1,KEEP_ALIVE))
                startTime = time.time()
                self.keepAliveCounter+=1

            elif self.keepAliveCounter == 1 and time.time() - startTime > 15:
                self.guiPrint("Sending keep alive #2 [after 35s]",1)
                self.sendPacket(createKeepAliveHeader(2,KEEP_ALIVE))
                startTime = time.time()
                self.keepAliveCounter+=1

            elif self.keepAliveCounter == 2 and time.time() - startTime > 10:
                self.keepAliveCounter+=1
                self.s.close()
                messagebox.showwarning("Disconnect","Server sa odpojil")
                self.gui.switchFrame(self.gui.clientFrame)
                return
            time.sleep(1)
        self.s.close()
    
    """ Send text message function called by GUI. Splits text into fragments of user-given size and sends them. """
    def sendTextMessage(self, text, MSS, error=None):
        text = bytearray(text, "utf-8")
        fragments = [text[i:i + MSS] for i in range(0, len(text), MSS)]
        numOfFragments = len(fragments)
        self.guiPrint(["Sending Text Message (%dB, %d fragments, MSS = %d)" %(len(text),numOfFragments,MSS), 'important'],3)
        self.send(fragments, len(text)/1000, numOfFragments, MSS, TEXT, error)

    """ Send file function called by GUI. Splits file into fragments of user-given size and sends them to server. """
    def sendFile(self, path, MSS, error=None):
        fragments = []
        with open(path, "rb") as f:
            fragment = f.read(MSS)
            while fragment:
                fragments.append(fragment)
                fragment = f.read(MSS)
        numOfFragments = len(fragments)
        self.guiPrint(["Sending File (%.2fkB, %d fragments, MSS = %d) [%s]" %(os.stat(path).st_size/1000,numOfFragments,MSS,path),'important'],3)
        self.send(fragments, os.stat(path).st_size/1000, numOfFragments, MSS, FILE, error, os.path.basename(path))

    """ Create initial packet for data transfer """
    def createInitial(self, fragmentAmount, MSS, Type, filename):
        if Type == FILE:
            return createHeader(calcCRC(filename.encode('utf-8')), createFlags(Type, CONN_INIT), MSS, fragmentAmount)+filename.encode('utf-8')
        else:
            return createHeader(0, createFlags(Type, CONN_INIT), MSS, fragmentAmount)

    """ Sends fragments + Last packet (CONN_FIN) using ARQ method. Needs to use mutex (threadlock) for successful sending fragments and receiving responses.
        Uses timers with timeout of 200ms in order to resend missed ACKs or fragments.
    """
    def sendFragments(self):
        while not self.finished:
            self.threadLock.acquire()
            timeouted=False
            for key,value in self.window.items():
                if value>0 and time.time() - value > 0.2:
                    
                    timeouted=True
                    fragment = self.fragments[key]
                    hdr = createHeader(calcCRC(fragment), createFlags(self.sendType), len(fragment), key)
                    self.window[key] = time.time()
                    # print(" > Timeout for fragment #%d/%d (%dB Data + %dB Header) exceeded, resending fragment.." %(key+1,self.fragmentAmount, len(fragment), len(hdr)))

                    self.s.sendto(hdr+fragment, self.serverAddress)
                    self.velkostRezie+=HeaderSize
                    self.guiPrint([" >> Timeout for fragment #%d/%d (%dB Data + %dB Header) exceeded, resending fragment.." %(key+1,self.fragmentAmount, len(fragment), len(hdr)),'warning'],3)
            # print(' > ',len(self.window),self.nextSeqN, self.fragmentAmount)   
            if self.windowLB <= self.nextSeqN < self.windowLB+windowSize and self.nextSeqN < self.fragmentAmount and not timeouted:     # packet from curr window can be sent
                fragment = self.fragments[self.nextSeqN]
                hdr = createHeader(calcCRC(fragment), createFlags(self.sendType), len(fragment), self.nextSeqN)
                if self.sendError == 0 and self.nextSeqN==0:
                    self.sendError = None
                    hdr = createHeader(0, createFlags(self.sendType), len(fragment), self.nextSeqN)
                self.window[self.nextSeqN] = time.time()
                # print(" > Sent fragment #%d (%dB Data + %dB Header)" %(self.nextSeqN+1, len(fragment), len(hdr))) 
                self.s.sendto(hdr+fragment, self.serverAddress)
                self.velkostRezie+=HeaderSize
                self.nextSeqN += 1
                self.guiPrint(" >> Sent fragment #%d/%d (%dB Data + %dB Header)" %(self.nextSeqN,self.fragmentAmount, len(fragment), len(hdr)),1)
            self.threadLock.release()

    """ Receiver thread for receiving responses from server. Uses mutex (thread lock) for successful simultaneous sending and receiving.
        Stops if both keep alives time out (will not get response).  
    """
    def receiver(self):
        while self.keepAliveCounter < 3:
            try:
                rawData, addr = self.s.recvfrom(MSS+HeaderSize)
                if isKeepAlive(rawData[2]):                                             # Keep Alive Response
                    keepAliveNumber, flags = decodeKeepAliveHeader(rawData[:KeepAliveHeaderSize])
                    if isACK(flags):
                        self.guiPrint("Received ACK for Keep Alive #%d"%(keepAliveNumber),1) 
                        if self.keepAliveCounter > 0:
                            self.keepAliveCounter=0
                    continue

                header, data = rawData[:HeaderSize],rawData[HeaderSize:]
                crc,flags,length,seqNumber = decodeHeader(header)
                if flags == CONN_INIT | ACK:                                            # Connection initialization
                    if not self.initializedConnection:
                        self.initializedConnection = True
                        self.frame.changeStatus(2)
                        self.guiPrint(["Connected to the server "+addr[0]+":"+str(addr[1]),'important'],3)

                elif isInitial(flags) and isACK(flags) and (isFile(flags) or isTextMessage(flags)): # Data transfer initialization
                    self.initializedDataTransfer = True

                elif flags == CONN_FIN:                                                 # Server disconnected. 
                    self.keepAliveCounter = 3
                    self.s.close()
                    self.gui.switchFrame(self.gui.clientFrame)
                    messagebox.showinfo("Disconnect","Server sa dobrovolne odpojil.")

                elif self.initializedDataTransfer and self.sendType & flags > 0:         # Transfer of fragments
                    self.threadLock.acquire()
                    if isACK(flags) and seqNumber in self.window.keys():                 # ACK for fragment in current window
                        if self.sendError == 1 and seqNumber==0:
                            self.sendError = None
                            self.guiPrint([" ~ ACK LOST for fragment #%d/%d" %(seqNumber+1,self.fragmentAmount),"warning"],3) 
                        else:
                            if seqNumber == self.windowLB:
                                self.windowLB += 1
                                self.window.pop(seqNumber)
                                for key,value in self.window.copy().items():
                                    if value > 0:
                                        break
                                    else:
                                        self.window.pop(key)
                                        self.windowLB += 1
                            else:
                                self.window[seqNumber]=-self.window[seqNumber]
                            if self.nextSeqN == self.fragmentAmount and len(self.window) == 0:
                                self.s.sendto(createHeader(0, createFlags(self.sendType,CONN_FIN), 0, 0), self.serverAddress)
                                self.velkostRezie+=HeaderSize
                            # print("Client - Received ACK for fragment #%d" %(seqNumber+1))
                            self.guiPrint(" << Received ACK for fragment #%d/%d" %(seqNumber+1,self.fragmentAmount),1)
                            
                    elif isNACK(flags):                                                   # NACK -> resending fragment
                        fragment = self.fragments[seqNumber]
                        hdr = createHeader(calcCRC(fragment), createFlags(self.sendType), len(fragment), seqNumber)
                        # print("Client - Received NACK for fragment #%d, resending fragment.." %(seqNumber+1))
                        
                        self.window[seqNumber]=time.time()
                        self.s.sendto(hdr+fragment, self.serverAddress)
                        self.velkostRezie+=HeaderSize
                        self.guiPrint([" << Received NACK for fragment #%d/%d" %(seqNumber+1,self.fragmentAmount),'warning'],3)
                        self.guiPrint(" >> Resent fragment #%d/%d (%dB Data + %dB Header)" %(seqNumber+1,self.fragmentAmount, len(fragment), len(hdr)),1)
                    elif isFinal(flags) and isACK(flags):                                  # Response for last packet (FINISH)
                        self.finished = True
                        # print("Text message successfuly sent.")
                        self.guiPrint([("Text Message" if self.sendType == TEXT else "File")+" successfuly sent",'success'],3)
                    else:
                        print("Unknown packet: ",hex(crc), bin(flags), length, seqNumber)
                    self.threadLock.release()

                else:
                    print("Unknown packet: ",hex(crc), bin(flags), length, seqNumber)
                
            except ConnectionResetError:
                # print("Could not connect to %s:%d"%(self.serverAddress[0],self.serverAddress[1]))
                self.guiPrint("Could not connect to %s:%d"%(self.serverAddress[0],self.serverAddress[1]),1)
            except OSError:
                pass
                # print("Socket closed!")
            
            
    """ Sender function for file / text message """
    def send(self, fragments, size, fragmentAmount, MSS, Type, error, filename=None):
        self.window = {}
        self.nextSeqN = 0
        self.windowLB = 0
        self.finished = False
        self.fragments = fragments
        self.fragmentAmount = fragmentAmount
        self.sendType = Type
        self.sendError = error
        sendStart = time.time()
        initialPacket = self.createInitial(fragmentAmount, MSS, Type, filename)
        self.sendPacket(initialPacket)
        stT = time.time(); sendCntr = 0
        while not self.initializedDataTransfer:
            if sendCntr == 2:
                self.guiPrint(["Could not initialize data transfer.","warning"],3)
                return
            if time.time() - stT > 1.5:
                self.sendPacket(initialPacket)
                stT=time.time()
                sendCntr += 1

        SendThread = Thread(target = self.sendFragments, daemon=True)

        SendThread.start()
        SendThread.join()
        self.initializedDataTransfer = False
        self.frame.reziaLabel["text"]=str(self.velkostRezie)+"B"

        sendEnd = time.time()
        self.guiPrint("Transfer of %.3fkB took %.3fsec"%(size, sendEnd-sendStart),1)

if __name__ == "__main__":
    GUI()