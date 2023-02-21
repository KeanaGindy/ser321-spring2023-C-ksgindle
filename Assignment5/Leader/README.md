<h1>PROJECT EXPLANATION </h1>
This demonstrated a simplified conensus algorithm between multiple nodes with the use of a leader node. This is exemplified through the creation of a
creditor or banking system with nodes acting as 'creditors' or 'banks' and the client acting as the user who wants to request credit or to pay back the credit.
The program responds accordingly depending on the balance of the creditors and the credit a client is requesting.

<h1>HOW TO RUN</h1>
*gradle runLeader --console=plain-q
*gradle runNode1 -Pmoney=1000 --console=plain-q
*gradle runNode2 -Pmoney=2000 --console=plain-q
*gradle runClient --console=plain-q

<h1>SCREENCAST</h1>
https://drive.google.com/file/d/1krPGESCAjpFXjN9Z2-DM_fph_GvrqRWL/view?usp=share_link

<h1>REQUIREMENTS</h1>
All requirements fulfilled besides 13 and 14. Additionally, it is not a very robust program. Therefore, the options such as 'credit' and 'pay-back' must be spelled correctly.

<h1>PROTOCOL</h1>

<h2>Requests</h2>

    -Type: clientID
    -Data: response
    
    -Type: ID
    -Data: IDresponse

    -Type: Choice
    -Data: response

    -Type: CreditChoice
    -Data: pay-back

    -Type: CreditChoice
    -Data: credit
    

<h2>Responses</h2>

    -Type: yes
    -Data: credit

    -Type: no
    -Data: credit

    -Type: yes
    -Data: payment

    -Type: no
    -Data: creditData
