import pcapy    
import os
import tkinter.scrolledtext as tkscrolled
from tkinter import *
from tkinter import filedialog

lineBreak = 16

pcapFilePath = "vzorky_pcap_na_analyzu\\trace-15.pcap"
configFilePath = "Configuration.txt"
config = {"Ethertypes":{"values":[],"protocols":[]}, "LSAPs":{"values":[],"protocols":[]},"IPs":{"values":[],"protocols":[]},"TCPs":{"values":[],"protocols":[]},"UDPs":{"values":[],"protocols":[]}}

komunikacie = {"http":[], "https":[], "telnet":[],"ssh":[],"ftp_riadiace":[],"ftp_datove":[], "tftp":[], "icmp":[], "arp":[]}
IPAdresses = {"Max":{}, "Values":{}}
pakety = []
analyzed = False

def loadConfig():
    f = open(configFilePath, "r")
    lines = f.read().splitlines()
    curr = ""
    for line in lines:
        if len(line) and line[0] == '#':
            curr = line[1:]
        elif(len(line)):
            config[curr]['values'].append(int(line.split(" ")[0],base=16))
            config[curr]['protocols'].append((line.split(" ", 1)[1]))

def arrayToMAC(arr):
    return "%.2X:%.2X:%.2X:%.2X:%.2X:%.2X" % (arr[0], arr[1], arr[2], arr[3], arr[4], arr[5])

def getEthertypeProtocol(protocolNumber):
    try:
        protocolIndex = config["Ethertypes"]["values"].index(protocolNumber)
        return config["Ethertypes"]["protocols"][protocolIndex]
    except:
        return None

def getIPProtocol(protocolNumber):
    try:
        transportProtocolIndex = config["IPs"]["values"].index(protocolNumber)
        return config["IPs"]["protocols"][transportProtocolIndex]
    except:
        return None

def byteArrayToIPAddress(data):
    return "%d.%d.%d.%d" % (data[0],data[1],data[2],data[3])

def getSAPProtocol(protocolNumber):
    try:
        SAPindex = config["LSAPs"]["values"].index(protocolNumber)
        return config["LSAPs"]["protocols"][SAPindex]
    except:
        return None

def getUDPProtocol(port):
    try:
        UDPProtocolIndex =  config["UDPs"]["values"].index(port)
        return config["UDPs"]["protocols"][UDPProtocolIndex]
    except:
        return None

def getTCPProtocol(port):
    try:
        TCPProtocolIndex =  config["TCPs"]["values"].index(port)
        return config["TCPs"]["protocols"][TCPProtocolIndex]
    except:
        return None

def getIPHeader(packetBody):
    pn = int(packetBody[23])
    IPHeaderLength = (packetBody[14] & 0x0F) * 4
    IPprotocol = getIPProtocol(pn)
    S_IP = byteArrayToIPAddress(packetBody[26:30])
    D_IP = byteArrayToIPAddress(packetBody[30:34])
    return {"Protocol": IPprotocol, "Protocol_number":pn, "Length":IPHeaderLength,  "SIP":S_IP,"DIP":D_IP}

def getPacketLength(packetHeader):
    return [packetHeader.getlen(),packetHeader.getlen()+4 if packetHeader.getlen()+4>64 else 64]

def getLinkLayer(packetBody):
    DMAC = arrayToMAC(packetBody[0:6])
    SMAC = arrayToMAC(packetBody[6:12])
    if(int.from_bytes(packetBody[12:14], byteorder="big")>0x5dc):
        Type = [0,"Ethernet II"]
    elif packetBody[14] == 0xAA:
        Type = [2,"IEEE 802.3 LLC + SNAP"]
    elif packetBody[14] == 0xFF:
        Type = [3,"IEEE 802.3 RAW"]
    else:
        Type = [1,"IEEE 802.3 LLC"]

    return {"DMAC":DMAC,"SMAC":SMAC,"Type":Type}

def getNetworkLayer(packet, linkLayerType):
    #ETH II
    if linkLayerType==0:
        pn = int.from_bytes(packet[12:14], byteorder="big")
        vnorenyProtokol = getEthertypeProtocol(pn)
    
    #LLC
    elif linkLayerType==1:
        pn = int.from_bytes(packet[20:22], byteorder="big")
        vnorenyProtokol = getSAPProtocol(pn)

    #SNAP
    elif linkLayerType==2:
        pn=0
        vnorenyProtokol = "IPX"

    # RAW
    elif linkLayerType==3:
        pn = int(packet[14])
        vnorenyProtokol = getEthertypeProtocol(pn)

    return {"Protocol": vnorenyProtokol, "Protocol_number":pn}

def getTransportLayer(packet, TLtype):
    IPHeaderLength = (packet[14] & 0x0F) * 4
    SPORT = int.from_bytes(packet[(14 + IPHeaderLength) : (16 + IPHeaderLength)], byteorder="big")
    DPORT = int.from_bytes(packet[(16 + IPHeaderLength) : (18 + IPHeaderLength)], byteorder="big")
    SERVERPORT = SPORT if SPORT < DPORT else DPORT
    if TLtype == "UDP":
        TLProtocol = getUDPProtocol(SERVERPORT)
    elif TLtype == "TCP":
        TLProtocol = getTCPProtocol(SERVERPORT)
    else:
        TLProtocol = None

    return {"SPORT":SPORT, "DPORT": DPORT, "SERVERPORT":SERVERPORT, "Protocol":TLProtocol}

def getICMPType(typeNumber):
    icmp_type_messages = {0: 'Echo Reply', 3: 'Destination Unreachable', 4: 'Source Quench', 5: 'Redirect', 8: 'Echo Request',
           9: 'Router Advertisement', 10: 'Router Selection', 11: 'Time Exceeded', 12: 'Parameter Problem',
           13: 'Timestamp', 14: 'Timestamp Reply', 15: 'Information Request', 16: 'Information Reply',
           17: 'Address Mask Request', 18: 'Address Mask Reply', 30: 'Traceroute'}
    return icmp_type_messages[typeNumber]

def printOutPacket(packet,printFunction):
    for index in range(len(packet)):
        if (index%int(lineBreak) == 0 and index):
            printFunction("\n")
        elif (index%(int(lineBreak)/2) == 0 and index):
            printFunction(" ")
        printFunction("%.2X " %(packet[index]))  
    printFunction("\n\n")


def makeGUI():
    loadConfig()
    window = Tk()
    window.title("PACKET ANALYZER")
    window.geometry("1000x600")
    window.filename=pcapFilePath
    SideBar = Frame(window)
    cfglbl = Label(SideBar, text=os.path.basename(configFilePath)+ " (default)")
    lbl = Label(SideBar, text=os.path.basename(window.filename) + " (default)")

    def open_config_file():
        global configFilePath
        fileN = filedialog.askopenfilename(initialdir="/", title="Select a config file")
        if len(fileN): 
            configFilePath = fileN
            cfglbl["text"]=os.path.basename(configFilePath)
            loadConfig()

    def open_pcap_file():
        global analyzed, komunikacie, IPAdresses, pakety
        fileN = filedialog.askopenfilename(initialdir="/", title="Select a pcap file")
        if len(fileN):
            komunikacie = {"http":[], "https":[], "telnet":[],"ssh":[],"ftp_riadiace":[],"ftp_datove":[], "tftp":[], "icmp":[], "arp":[]}
            IPAdresses = {"Max":{}, "Values":{}}
            pakety = []
            analyzed = False
            window.filename = fileN
            lbl["text"]=os.path.basename(window.filename)

    choices = [16,32]
    printoutLength = StringVar(window)
    printoutLength.set(choices[0])
    btn = Button(SideBar, text="Otvorit pcap", command=open_pcap_file)
    cfgbtn = Button(SideBar, text="Nacitat config", command=open_config_file)
    textF = tkscrolled.ScrolledText(window)

    def callPrintFunction(outputField,commType):
        if not len(pcapFilePath) or not len(configFilePath):
            return
        global lineBreak
        textF.configure(state='normal')
        lineBreak = int(printoutLength.get())
        textF.delete(1.0, END)
        if analyzed == False:
            analyze(window.filename)
        communicationPrinter(outputField, commType)
        textF.configure(state='disabled')

    vypisB = Button(SideBar, text="Výpis všetkých rámcov",command=lambda:(callPrintFunction(textF,"all")))
    bod4 = Frame(SideBar)
    vypisKomL = Label(SideBar, text="Vypis komunikacie:")
    scrollFrame = Frame(SideBar)
    e1 = Entry(scrollFrame, width=6)

    def findFrame():
        if e1.get().isdigit() and len(e1.get()):
            pos = textF.search("Frame #"+e1.get(), "1.0", END)
            if pos != "":
                textF.yview(pos)

    btnss = Button(scrollFrame, text="Jump to ", command=findFrame)
    w = OptionMenu(SideBar, printoutLength, *choices)
    llb = Label(SideBar, text='Pocet byteov vo vypise: ')
    httpB = Button(bod4, text="HTTP", command = lambda:callPrintFunction(textF,"http"))
    httpsB = Button(bod4, text="HTTPS", command = lambda:callPrintFunction(textF,"https"))
    telnetB = Button(bod4, text="TELNET", command = lambda:callPrintFunction(textF,"telnet"))
    sshB = Button(bod4, text="SSH", command = lambda:callPrintFunction(textF,"ssh"))
    ftprB = Button(bod4, text="FTP riadiace", command = lambda:callPrintFunction(textF,"ftp_riadiace"))
    ftpdB = Button(bod4, text="FTP dátové", command = lambda:callPrintFunction(textF,"ftp_datove"))
    tftpB = Button(bod4, text="TFTP", command = lambda:callPrintFunction(textF,"tftp"))
    icmpB = Button(bod4, text="ICMP", command = lambda:callPrintFunction(textF,"icmp"))
    arpB = Button(bod4, text="ARP", command = lambda:callPrintFunction(textF,"arp"))
    textF.pack(side=LEFT, expand=True, fill=BOTH)
    SideBar.pack(fill=BOTH, expand=True)
    llb.pack(side=TOP)
    w.pack(side=TOP)
    cfgbtn.pack(side=TOP, pady=(10,5))
    cfglbl.pack(side=TOP)
    btn.pack(side=TOP, pady=(10,5))
    lbl.pack(side=TOP)
    scrollFrame.pack(side=TOP, pady=(20,0))
    btnss.pack(side=LEFT, padx=(0, 5))
    e1.pack(side=LEFT)
    vypisB.pack(pady=(50,15))
    vypisKomL.pack()
    bod4.pack()
    httpB.grid(row=0,column=0, padx=5, pady=2)
    httpsB.grid(row=0,column=1, padx=5, pady=2)
    telnetB.grid(row=1,column=0, padx=5, pady=2)
    sshB.grid(row=1,column=1, padx=5, pady=2)
    ftprB.grid(row=2,column=0, padx=5, pady=2)
    ftpdB.grid(row=2,column=1, padx=5, pady=2)
    tftpB.grid(row=3,column=0, padx=5, pady=2)
    icmpB.grid(row=3,column=1, padx=5, pady=2)
    arpB.grid(row=4,column=0, padx=5, pady=2, columnspan=2)
    window.mainloop()

def printoutComm(comm, cprint):
    if len(comm) > 20:
        comm = comm[0:10] + comm[len(comm)-10 : len(comm)]
    for paket in comm:
            if "L3" in paket and "ARP" in paket["L3"]:
                if paket["L3"]["ARP"]["Operation"] == 1:
                    cprint(" ~ ARP-REQUEST, IP: "+paket["L3"]["ARP"]["DIP"]+", MAC: ??? ~\n")
                    cprint(" ~ Source IP : "+paket["L3"]["ARP"]["SIP"]+", Target IP: "+paket["L3"]["ARP"]["DIP"]+" ~\n")
                else:
                    cprint(" ~ ARP-REPLY, IP : "+paket["L3"]["ARP"]["SIP"]+", MAC: "+paket["L3"]["ARP"]["SMAC"]+" ~\n")
                    cprint(" ~ Source IP : "+paket["L3"]["ARP"]["SIP"]+", Target IP: "+paket["L3"]["ARP"]["DIP"]+" ~\n")
            if "ICMP" in paket:
                cprint(" >> " +getICMPType(paket["ICMP"]["Type"]) + " << \n")
            cprint('Frame #%s\n'%(paket["Info"]["Number"]))
            cprint(' > Length from PCAP API: %d B\n'%(paket["Info"]["Length_API"]))
            cprint(' > Length on medium:     %d B\n'%(paket["Info"]["Length_medium"]))
            cprint("Link Layer:\n")
            if paket["L2"]["Type"] is not None:
                cprint(" > "+paket["L2"]["Type"][1]+"\n")
            cprint(" > Source MAC:      "+paket["L2"]["SMAC"]+"\n")
            cprint(" > Destination MAC: "+paket["L2"]["DMAC"]+"\n")
            if "L3" in paket and paket["L3"]["Protocol"] is not None:
                cprint("Network Layer:\n")
                cprint(" > "+paket["L3"]["Protocol"]+"\n")
                if "IP" in paket["L3"]:
                    cprint(" > Source IP Address:      "+paket["L3"]["IP"]["SIP"]+"\n")
                    cprint(" > Destination IP Address: "+paket["L3"]["IP"]["DIP"]+"\n")
                    if paket["L3"]["IP"]["Protocol"] is not None:
                        cprint("Transport Layer:\n")
                        cprint(" > "+paket["L3"]["IP"]["Protocol"]+"\n")
                        cprint(" > Source port:      "+str(paket["L4"]["SPORT"])+"\n")
                        cprint(" > Destination port: "+str(paket["L4"]["DPORT"])+"\n")
                        cprint(" > "+str(paket["L4"]["Protocol"])+"\n")
                    if "ICMP" in paket:
                        cprint("Transport Layer:\n")
                        cprint(" > ICMP\n")
                        cprint(" > " +getICMPType(paket["ICMP"]["Type"]) + "\n")
            printOutPacket(paket["Info"]["Body"], cprint)

def communicationPrinter(*args):
    # args 0 - ScrollText / None , 1 - communication protocol,
    if (len(args)>0 and type(args[0]) == type(tkscrolled.ScrolledText())):
        def cprint(comm):
            args[0].insert(END, comm)
    else:
        def cprint(comm):
            print(comm, end="")

    if (args[1].lower() == "all"):
        for paket in pakety:
            cprint('Frame #%s\n'%(paket["Info"]["Number"]))
            cprint(' > Length from PCAP API: %d B\n'%(paket["Info"]["Length_API"]))
            cprint(' > Length on medium:     %d B\n'%(paket["Info"]["Length_medium"]))
            cprint("Link Layer:\n")
            if paket["L2"]["Type"] is not None:
                cprint(" > "+paket["L2"]["Type"][1]+"\n")
            cprint(" > Source MAC:      "+paket["L2"]["SMAC"]+"\n")
            cprint(" > Destination MAC: "+paket["L2"]["DMAC"]+"\n")
            if "L3" in paket and paket["L3"]["Protocol"] is not None:
                cprint("Network Layer:\n")
                cprint(" > "+paket["L3"]["Protocol"]+"\n")
                if "IP" in paket["L3"]:
                    cprint(" > Source IP Address:      "+paket["L3"]["IP"]["SIP"]+"\n")
                    cprint(" > Destination IP Address: "+paket["L3"]["IP"]["DIP"]+"\n")
                    if paket["L3"]["IP"]["Protocol"] is not None:
                        cprint("Transport Layer:\n")
                        cprint(" > "+paket["L3"]["IP"]["Protocol"]+"\n")
            printOutPacket(paket["Info"]["Body"], cprint)
            cprint("\n")
        
        cprint("IP Addresses of destination nodes:\n")
        for ips in IPAdresses["Values"].keys():
            cprint(" "+ips+"\n")
        cprint("\nIP Address(es) of destination node(s) with the most received packets:\n")
        for ips in IPAdresses["Max"]["addresses"]:
            cprint(" %s   (%d packets)\n"%(ips, IPAdresses["Max"]["value"]))

    elif (args[1].lower() in ["http","https","ssh","telnet","ftp_riadiace", "ftp_datove"]): # tcp
        notcomplete = False
        complete = False
        for index, kom in enumerate(komunikacie[args[1].lower()]):
            if kom["status"] == 0x11 and not complete:
                complete = True
                cprint(" "*lineBreak +"<<< Complete communication #%d >>>\n" %(index+1))
                printoutComm(kom["packets"], cprint)
                cprint(" "*lineBreak +"<<< -------------------- >>>\n\n")
            elif kom["status"] == 0x10 and not notcomplete:
                notcomplete = True
                cprint(" "*lineBreak +"<<< Incomplete communication #%d >>>\n" %(index+1))
                printoutComm(kom["packets"], cprint)
                cprint(" "*lineBreak +"<<< -------------------- >>>\n\n")
            if complete and notcomplete:
                break

        if not complete:
            cprint(" "*lineBreak +"<<< Complete communication not found >>>\n\n")    
        if not notcomplete:
            cprint(" "*lineBreak +"<<< Incomplete communication not found >>>\n\n")    

    elif (args[1].lower() in ["tftp", "icmp"]): # tftp, icmp
        found = False
        for index, kom in enumerate(komunikacie[args[1].lower()]):
            found = True
            cprint(" "*lineBreak +"<<< Communication #%d >>>\n" %(index+1))
            printoutComm(kom, cprint)
            cprint(" "*lineBreak +"<<< -------------------- >>>\n\n")
        if not found:
            cprint(" "*lineBreak +"<<< Communication not found >>>\n\n")    

    elif (args[1].lower() == "arp"): # arp
        found = False
        for index, kom in enumerate(komunikacie[args[1].lower()]):
            found = True
            cprint(" "*lineBreak + "<<< Communication #%d >>>\n" %(index+1))
            printoutComm(kom["REQ"], cprint)
            printoutComm(kom["REPLY"], cprint)
            cprint(" "*lineBreak +"<<< -------------------- >>>\n\n")
        if not found:
            cprint(" "*lineBreak +"<<< Communication not found >>>\n\n")

def matchPortsAndIPs(p1, p2):
    p1_sip = p1["L3"]["IP"]["SIP"]
    p2_sip = p2["L3"]["IP"]["SIP"]
    p1_dip = p1["L3"]["IP"]["DIP"]
    p2_dip = p2["L3"]["IP"]["DIP"]
    p1_sport = p1["L4"]["SPORT"]
    p2_sport = p2["L4"]["SPORT"]
    p1_dport = p1["L4"]["DPORT"]
    p2_dport = p2["L4"]["DPORT"]
    if ((p1_sip == p2_sip and p1_dip == p2_dip) or (p1_sip == p2_dip and p1_dip == p2_sip)) and ((p1_sport == p2_sport and p1_dport == p2_dport) or (p1_sport == p2_dport and p1_dport == p2_sport)):
        return True
    return False

def matchOnePortAndIPs(p1, p2):
    p1_sip = p1["L3"]["IP"]["SIP"]
    p2_sip = p2["L3"]["IP"]["SIP"]
    p1_dip = p1["L3"]["IP"]["DIP"]
    p2_dip = p2["L3"]["IP"]["DIP"]
    p1_sport = p1["L4"]["SPORT"]
    p2_sport = p2["L4"]["SPORT"]
    p1_dport = p1["L4"]["DPORT"]
    if ((p1_sip == p2_sip and p1_dip == p2_dip) or (p1_sip == p2_dip and p1_dip == p2_sip)) and (p1_sport == p2_sport or p1_dport == p2_sport):
        return True
    return False

def checkStartHandshake(comm):
    if len(comm["packets"]) > 2:
        for ind in range(len(comm["packets"])-2):
            flag1 = comm["packets"][ind]["Info"]["Body"][27+comm["packets"][ind]["L3"]["IP"]["Length"]] & 0x02 #syn
            flag2 = comm["packets"][ind+1]["Info"]["Body"][27+comm["packets"][ind+1]["L3"]["IP"]["Length"]] & 0x12 #syn + ack
            flag3 = comm["packets"][ind+2]["Info"]["Body"][27+comm["packets"][ind+2]["L3"]["IP"]["Length"]] & 0x10 #ack
            if (flag1 and flag2 and flag3):
                comm["status"] |= 0x10

def checkEndHandshake(comm):
    comm_size = len(comm["packets"])
    if comm_size>0:
        finHandshake = 0b000
        for pckt in comm["packets"]:
            flag = pckt["Info"]["Body"][27+pckt["L3"]["IP"]["Length"]]
            if flag & 0x04: # rst
                comm["status"] |= 0x01
                return

            if flag & 0x1 and finHandshake == 0b000: # fin
                finHandshake = 0b100
            elif (flag & 0x11 == 0x11) and finHandshake == 0b100:
                finHandshake = 0b110
            elif flag & 0x10 and finHandshake == 0b110:
                comm["status"] |= 0x01
                return



def checkCommunicationExistence(packet,typ):
    global komunikacie
    if len(komunikacie[typ]) > 0:
        for comm in komunikacie[typ]:
            if (comm["status"] & 1 == 0) and matchPortsAndIPs(packet, comm["packets"][0]):
                comm["packets"].append(packet)                
                if not (comm["status"] & 0x10):
                    checkStartHandshake(comm)
                if not (comm["status"] & 0x01):
                    checkEndHandshake(comm)
                return
        komunikacie[typ].append({"status":0,"packets":[packet]})       
    else:
        komunikacie[typ].append({"status":0,"packets":[packet]})

def checkCommunication(packet):
    if packet["L4"]["SERVERPORT"] == 80 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"http")
    elif packet["L4"]["SERVERPORT"] == 443 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"https")
    elif packet["L4"]["SERVERPORT"] == 20 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"ftp_datove")
    elif packet["L4"]["SERVERPORT"] == 21 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"ftp_riadiace")
    elif packet["L4"]["SERVERPORT"] == 22 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"ssh")
    elif packet["L4"]["SERVERPORT"] == 23 and packet["L4"]["Protocol"] is not None:
        checkCommunicationExistence(packet,"telnet")

def checkTFTPComm(packet):
    global komunikacie
    opcode = int.from_bytes(packet["Info"]["Body"][(22+packet["L3"]["IP"]["Length"]):(24+packet["L3"]["IP"]["Length"])], byteorder="big")
    if (opcode == 1 or opcode == 2) and packet["L4"]["SERVERPORT"] == 69 and packet["L4"]["Protocol"] is not None: # write or read request
        komunikacie["tftp"].append([packet])
    elif (opcode >= 3 and opcode <= 5):
        for kom in komunikacie["tftp"]:
            if len(kom) == 1 and matchOnePortAndIPs(packet, kom[0]):
                kom.append(packet)
            elif len(kom) > 1 and matchPortsAndIPs(packet, kom[1]):
                kom.append(packet)

def checkICMPComm(packet):
    global komunikacie
    HL = packet["L3"]["IP"]["Length"]
    icmp_type = packet["Info"]["Body"][14+HL]
    icmp_code = packet["Info"]["Body"][15+HL]
    icmp_id = int.from_bytes(packet["Info"]["Body"][(18+HL):(20+HL)], byteorder="big")
    icmp_sn = int.from_bytes(packet["Info"]["Body"][(20+HL):(22+HL)], byteorder="big")
    packet["ICMP"] = {"ID":icmp_id, "SN":icmp_sn, "Type": icmp_type,"Code":icmp_code}
    if (icmp_type == 8): #req
        komunikacie["icmp"].append([packet])
    elif (icmp_type == 0): #rep
        for comm in komunikacie["icmp"]:
            if comm[0]["ICMP"]["ID"] == icmp_id and comm[0]["ICMP"]["SN"] == icmp_sn and comm[0]["ICMP"]["Type"]==8:
                comm.append(packet)
    else:
        found = False
        for comm in komunikacie["icmp"]:
            if comm[0]["ICMP"]["Type"] != 8 and comm[0]["ICMP"]["Type"] != 0:
                cip = comm[0]["L3"]["IP"]
                pcip = packet["L3"]["IP"]
                if (cip["SIP"] == pcip["SIP"] and cip["DIP"] == pcip["DIP"]) or (cip["SIP"] == pcip["DIP"] and cip["DIP"] == pcip["SIP"]):
                    comm.append(packet)
                    found = True
        if not found:
            komunikacie["icmp"].append([packet])       

def checkARPComm(packet):
    global komunikacie
    operation = int.from_bytes(packet["Info"]["Body"][20:22], 'big')
    SIP = byteArrayToIPAddress(packet["Info"]["Body"][28:32])
    DIP = byteArrayToIPAddress(packet["Info"]["Body"][38:42])
    SMAC = arrayToMAC(packet["Info"]["Body"][22:28])
    DMAC = arrayToMAC(packet["Info"]["Body"][32:38])
    packet["L3"]["ARP"] = {"Operation" : operation, "SIP":SIP,"DIP":DIP,"SMAC":SMAC,"DMAC":DMAC}
    if operation == 1: # req
        for comm in komunikacie["arp"]:
            if len(comm["REQ"]) > 0 and not comm["Done"] and comm["REQ"][0]["L3"]["ARP"]["SIP"] == SIP and comm["REQ"][0]["L3"]["ARP"]["DIP"] == DIP:
                comm["REQ"].append(packet)
                return
        komunikacie["arp"].append({"REQ":[packet], "REPLY": [], "Done" : False})
    elif operation == 2: # reply
        for comm in komunikacie["arp"]:
            if len(comm["REQ"]) > 0 and not comm["Done"] and comm["REQ"][0]["L3"]["ARP"]["DIP"] == SIP and comm["REQ"][0]["L3"]["ARP"]["SIP"] == DIP:
                comm["REPLY"].append(packet)
                comm["Done"] = True
                return
            elif len(comm["REPLY"]) > 0 and not comm["Done"] and comm["REPLY"][0]["L3"]["ARP"]["SIP"] == SIP and comm["REPLY"][0]["L3"]["ARP"]["DIP"] == DIP:
                comm["REPLY"].append(packet)
                return
        komunikacie["arp"].append({"REQ":[], "REPLY": [packet], "Done" : False})

def analyze(filePath):
    global analyzed
    analyzed = True
    count = 0
    packetReader = pcapy.open_offline(filePath)
    (Pkthdr, Pktbody) = packetReader.next()
    while (Pkthdr):
        packet = {}
        count+=1
        lengthPcap, lengthMedium = getPacketLength(Pkthdr)
        packet["Info"] = {"Number": count, "Length_API":lengthPcap, "Length_medium":lengthMedium, "Body": Pktbody}
        packet["L2"] = getLinkLayer(Pktbody)
        PacketLayer3 = getNetworkLayer(Pktbody, packet["L2"]["Type"][0])
        if PacketLayer3["Protocol"] is not None:
            packet["L3"] = PacketLayer3
            
            # Ethernet
            if packet["L2"]["Type"][0] == 0:
                # IPv4
                
                if packet["L3"]["Protocol_number"] == 2048:
                        packet["L3"]["IP"] = getIPHeader(Pktbody)
                        
                        if packet["L3"]["IP"]["Protocol"] is not None:
                            packet["L4"] = getTransportLayer(Pktbody,packet["L3"]["IP"]["Protocol"])
                            if packet["L3"]["IP"]["Protocol"] == "TCP":
                                checkCommunication(packet) 
                            if packet["L3"]["IP"]["Protocol"] == "UDP":
                                checkTFTPComm(packet)
                            if packet["L3"]["IP"]["Protocol"] == "ICMP":
                                checkICMPComm(packet)

                        IPAdresses["Values"][packet["L3"]["IP"]["DIP"]] = 1 if IPAdresses["Values"].get(packet["L3"]["IP"]["DIP"]) is None else IPAdresses["Values"].get(packet["L3"]["IP"]["DIP"])+1
                
                #ARP
                if packet["L3"]["Protocol_number"] == 0x806:
                    checkARPComm(packet)

        pakety.append(packet)
        (Pkthdr, Pktbody) = packetReader.next()

    IPAdresses["Max"] = {"value":0,"addresses":[]}
    for key, value in IPAdresses["Values"].items() :
        if value > IPAdresses["Max"]["value"]:
            IPAdresses["Max"]["value"] = value
            IPAdresses["Max"]["addresses"] = [key]
        elif value == IPAdresses["Max"]["value"]:
            IPAdresses["Max"]["addresses"].append(key)
     
makeGUI()
