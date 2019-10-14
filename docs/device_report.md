# Device 9a:02:57:1e:8f:01, *** Make *** *** Model ***

## Test Roles

|  Role  |      Name              | Status |
|--------|------------------------|--------|
|Operator| *** Operator Name *** |        |
|Approver| *** Approver Name *** |        |

## Test Iteration

| Test             |                        |
|------------------|------------------------|
| Test report start date | 2019-10-14 14:25:45+00:00 |
| Test report end date   | 2019-10-14 14:33:27+00:00 |
| DAQ version      | 1.0.1 |
| Attempt number   | 1 |

## Device Identification

| Device            | Entry              |
|-------------------|--------------------|
| Name              | *** Name *** |
| GUID              | *** GUID *** |
| MAC addr          | 9a:02:57:1e:8f:01 |
| Hostname          | *** Network Hostname *** |
| Type              | *** Type *** |
| Make              | *** Make *** |
| Model             | *** Model *** |
| Serial Number     | *** Serial *** |
| Firmware Version  | *** Firmware Version *** |

## Device Description

![Image of device](*** Device Image URL ***)

*** Device Description ***


### Device documentation

[Device datasheets](*** Device Datasheets URL ***)
[Device manuals](*** Device Manuals URL ***)

## Report summary

Overall device result FAIL

|Category|Result|
|---|---|
|Security|0/1|
|Other|1/2|
|Connectivity|n/a|

|Expectation|pass|fail|skip|gone|
|---|---|---|---|---|
|Required|1|1|0|0|
|Recommended|0|1|0|0|
|Other|2|1|15|2|

|Result|Test|Category|Expectation|Notes|
|---|---|---|---|---|
|skip|base.switch.ping|Other|Other||
|pass|base.target.ping|Connectivity|Required|target|
|skip|cloud.udmi.pointset|Other|Other|No device id.|
|fail|connection.mac_oui|Other|Other||
|skip|connection.port_duplex|Other|Other||
|skip|connection.port_link|Other|Other||
|skip|connection.port_speed|Other|Other||
|fail|network.brute|Security|Required||
|skip|poe.negotiation|Other|Other||
|skip|poe.power|Other|Other||
|skip|poe.support|Other|Other||
|skip|protocol.bacnet.pic|Other|Other|Bacnet device not found... Pics check cannot be performed.|
|skip|protocol.bacnet.version|Other|Other|Bacnet device not found.|
|skip|security.firmware|Other|Other|Could not retrieve a firmware version with nmap.|
|skip|security.passwords.http|Other|Other|Device does not have a valid mac address|
|skip|security.passwords.https|Other|Other|Device does not have a valid mac address|
|skip|security.passwords.ssh|Other|Other|Device does not have a valid mac address|
|skip|security.passwords.telnet|Other|Other|Device does not have a valid mac address|
|fail|security.ports.nmap|Security|Recommended||
|pass|security.tls.v3|Other|Other||
|pass|security.x509|Other|Other||
|gone|unknown.fake.llama|Other|Other||
|gone|unknown.fake.monkey|Other|Other||


## Module ping

```
Baseline ping test report
%% 73 packets captured.
RESULT skip base.switch.ping
RESULT pass base.target.ping target %% 10.20.24.164
```

## Module nmap

```
Failing 80 open tcp http ,
Allowing 443 open tcp https ,
Allowing 10000 open tcp snet-sensor-mgmt
RESULT fail security.ports.nmap
```

## Module brute

```
Username:manager
Password:friend
Login success!
RESULT fail network.brute
```

## Module discover

```
--------------------
security.firmware
--------------------
Automatic bacnet firmware scan using nmap
--------------------
PORT      STATE  SERVICE
47808/udp closed bacnet
MAC Address: 9A:02:57:1E:8F:01 (Unknown)
Firmware test complete
--------------------
RESULT skip security.firmware Could not retrieve a firmware version with nmap.
```

## Module switch

```
LOCAL_IP not configured, assuming no network switch.
RESULT skip connection.port_link
RESULT skip connection.port_speed
RESULT skip connection.port_duplex
RESULT skip poe.power
RESULT skip poe.negotiation
RESULT skip poe.support
```

## Module macoui

```
Mac OUI Test
RESULT fail connection.mac_oui
```

## Module bacext

```
RESULT skip protocol.bacnet.version Bacnet device not found.
RESULT skip protocol.bacnet.pic Bacnet device not found... Pics check cannot be performed.
```

## Module tls

```
Cipher:
TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
Certificate is active for current date.
RESULT pass security.tls.v3
RESULT pass security.x509

Certificate:
[
[
  Version: V1
  Subject: CN=127.0.0.1, OU=Software, O=ExcelRedstone, L=KingsX, ST=London, C=GB
  Signature Algorithm: SHA256withRSA, OID = 1.2.840.113549.1.1.11

  Key:  Sun RSA public key, 2048 bits
  modulus: 27835674148796640929724026566114763869360656705850882061015196003221107891532689407992218607271078514248415151866600035566177715593600058371614408807587352737279522434419204983270212233652952332792492309515298016934083574503654406312982851379805486456657733190767939201489641017844630131423678736325919088952348200164075305099391035805312819565797326715131401255224455315194890605562889240539585147205508324923391651194376596086063444470104795779847540704249238861008788831415239613679189301067634237175395452134073723134341558529626101963838672947339543300667464800065852024912360604893000821528951555582664643119109
  public exponent: 65537
  Validity: [From: Wed Jun 05 12:22:02 GMT 2019,
               To: Thu Jun 04 12:22:02 GMT 2020]
  Issuer: CN=127.0.0.1, OU=Software, O=ExcelRedstone, L=KingsX, ST=London, C=GB
  SerialNumber: [    99e686db 69d35606]

]
  Algorithm: [SHA256withRSA]
  Signature:
0000: 4B BE A8 EB E9 8F DF D6   CB A3 A0 F1 BB 41 D5 CD  K............A..
0010: 8D E0 1F 90 81 13 AC 00   73 3C 98 3E D6 60 39 F9  ........s<.>.`9.
0020: F1 37 AE BE 66 5C 74 41   E1 09 7F 73 96 85 A9 72  .7..f\tA...s...r
0030: 64 6C E5 0B 3E 80 88 42   2E 4A 1D 72 A7 71 9C C4  dl..>..B.J.r.q..
0040: 2C 5B 51 92 B5 1D EA 6B   3F BA EA DE 63 F1 88 71  ,[Q....k?...c..q
0050: 55 39 DE A4 09 09 D9 A6   B7 C5 B8 B0 44 33 38 48  U9..........D38H
0060: EB 32 3B AE E0 E1 47 A5   8E B5 C2 96 2F 53 AE 07  .2;...G...../S..
0070: 2B A7 28 72 34 80 26 2E   DA CC A8 38 4F D1 32 07  +.(r4.&....8O.2.
0080: 21 8D 22 8D 8F F1 8B FF   0E FB 65 82 B6 E0 3F 66  !.".......e...?f
0090: 81 D6 CC CF DD 46 62 B6   E4 C2 82 FF 30 8E 27 C9  .....Fb.....0.'.
00A0: 62 E4 D9 64 B4 8F 41 AE   21 C0 BC 4B E5 52 39 1E  b..d..A.!..K.R9.
00B0: FF D9 C3 62 10 36 5A 1E   AA CF 5B BE 3D 7C 36 8D  ...b.6Z...[.=.6.
00C0: 20 A0 20 FD CF 22 15 4F   54 36 3C 37 77 F1 DD A1   . ..".OT6<7w...
00D0: E7 26 05 71 9A F6 1B 15   AD D8 C8 5B E1 84 A6 34  .&.q.......[...4
00E0: AE 26 5D 3D F6 44 14 53   57 D3 86 85 88 7D 6B B5  .&]=.D.SW.....k.
00F0: E2 19 5D 5F 36 73 73 12   CC 96 62 38 F3 C0 62 18  ..]_6ss...b8..b.

]
```

## Module password

```
--------------------
security.passwords.http
--------------------
Verify all default password have been updated. Ensure new Google provided passwords are set
--------------------
Redacted Log
--------------------
RESULT skip security.passwords.http Device does not have a valid mac address

--------------------
security.passwords.https
--------------------
Verify all default password have been updated. Ensure new Google provided passwords are set
--------------------
Redacted Log
--------------------
RESULT skip security.passwords.https Device does not have a valid mac address

--------------------
security.passwords.telnet
--------------------
Verify all default password have been updated. Ensure new Google provided passwords are set
--------------------
Redacted Log
--------------------
RESULT skip security.passwords.telnet Device does not have a valid mac address

--------------------
security.passwords.ssh
--------------------
Verify all default password have been updated. Ensure new Google provided passwords are set
--------------------
Redacted Log
--------------------
RESULT skip security.passwords.ssh Device does not have a valid mac address

```

## Module udmi

```
RESULT skip cloud.udmi.pointset No device id.
```

## Report complete

