# XML Format Transparenzsoftware (TPS)

## Description

The XML format, which can be read by the TPS, is a container for different kinds of other formats, like OCMF, SML, etc.
It can contain one dataset plus one public key, that is used to verify the signature of the dataset.
If may also contain multiple datasets in one single file, which are signed, and each dataset has it's own public key.

## Example
```
<?xml version="1.0" encoding="UTF-8"?>
<values>
   <value transactionId="1" context="Transaction.Begin">
      <signedData format="OCMF" encoding="plain" transactionId="29">OCMF|{"FV":"1.0","GI":"KEBA_KCP30","GS":"17619300","GV":"2.8.5","PG":"T32","IS":false,"IL":"NONE","IF":["RFID_NONE","OCPP_NONE","ISO15118_NONE","PLMN_NONE"],"IT":"NONE","ID":"","RD":[{"TM":"2019-08-13T10:03:15,000+0000 I","TX":"B","EF":"","ST":"G","RV":0.2596,"RI":"1-b:1.8.0","RU":"kWh"},{"TM":"2019-08-13T10:03:36,000+0000 R","TX":"E","EF":"","ST":"G","RV":0.2597,"RI":"1-b:1.8.0","RU":"kWh"}]}|{"SD":"304502200E2F107C987A300AC1695CA89EA149A8CDFA16188AF0A33EE64B67964AA943F9022100889A72B6D65364BEA8562E7F6A0253157ACFF84FE4929A93B5964D23C4265699"}</signedData>
        <publicKey encoding="hex">3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE</publicKey>
    </value>
</values>
```
## Basic structure
```
<values>
   <value>
      <signedData>...</signedData>
      <pulicKey>...</publicKey>
   </value>
   <value>
   ...
   </value>
</values>
```
The `values` element has no attributes and contains 1 or many `<value>` elements.

## value Element
The `value` element has the following attributes:
 * `transactionId`: the number of the dataset, will be shown at the left side of the TPS in a tree.
 * `context`: a description of the dataset
The `value` element contains one `signedData` and one optional `publicKey` element.
If the public key is not present, the user must find the right public key and copy it into the form field to verify the integrity of the dataset.

## signedData Element
The `signedData` element has the following attributes
 * `format`
 * `encoding`
 * `transactionId`
The `signedData` element encloses the data of the dataset. If it is a string-based format, no line feeds or white spaces have to be at the beginning or end of the element, else the signature can not be verified.
