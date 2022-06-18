{// avoid variables ending up in the global scope
    let userDetails, contacts, accountList, accountDetails, transferForm,
        transferResult, addAccount, messageContainer, pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("id") == null ||
            sessionStorage.getItem("name") == null ||
            sessionStorage.getItem("surname") == null) {
            window.location.href = "Login.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh();
        } // display initial content
    }, false);

    function UserDetails(_ID, _name, _surname, userWelcomeContainer, personalIDContainer){
        this.ID = _ID;
        this.name = _name;
        this.surname = _surname;
        this.show = () => {
            userWelcomeContainer.textContent = this.surname + ", " + this.name;
            personalIDContainer.textContent = "Personal ID: " + this.ID;
        }
    }

    function AccountList(_summaryContainer, _accountListContainer, _alertBox){
        this.summaryContainer = _summaryContainer;
        this.accountListContainer = _accountListContainer;
        this.alertBox = _alertBox;

        this.reset = () => {
            this.summaryContainer.style.visibility = "hidden";
            this.accountListContainer.style.visibility = "hidden";
        }

        //gets the account list
        this.show = () => {
            let self = this;
            makeCall("GET","GetAccounts",null,
                (req) => {
                    if(req.readyState === 4){
                        let message = req.responseText;
                        if(req.status === 200){
                            let resp = JSON.parse(req.responseText);
                            self.update(resp.accounts);
                        }
                        else{
                            self.alertBox.textContent = message;
                        }
                    }
                });
        }

        this.update = (arrayAccounts) => {
            let row,anchor,accountDiv,accountNameDiv,spaceDiv,accountBalanceDiv,cashSum;
            this.accountListContainer.innerHTML = ""; //empty the div
            cashSum = 0;
            //build the updated list
            let self = this;
            arrayAccounts.forEach((account) => {

                //Builds a div for each account
                accountDiv = document.createElement("div");
                accountDiv.className = "account_entry d-flex align-items-center mb-3";
                row = document.createElement("div");
                row.className = "row entry_data";
                accountDiv.appendChild(row);
                accountNameDiv = document.createElement("div");
                accountNameDiv.className = "col-md-8";
                accountNameDiv.textContent = account.name;
                accountDiv.appendChild(accountNameDiv);
                spaceDiv = document.createElement("div");
                spaceDiv.className = "col-md-2";
                accountDiv.appendChild(spaceDiv);
                accountBalanceDiv = document.createElement("div");
                accountBalanceDiv.className = "col-md-2 account_balance";
                accountDiv.appendChild(accountBalanceDiv);
                accountBalanceDiv.textContent = account.balance + "€";

                //Registers event on the account div
                accountDiv.addEventListener("click", (e) => {
                    accountDetails.show(account.ID);
                }, false);

                self.accountListContainer.appendChild(accountDiv);
                cashSum += account.balance;
            });
            this.summaryContainer.textContent = "Your Summary: ".concat(cashSum).concat("€");
            this.summaryContainer.style.visibility = "visible";
            this.accountListContainer.style.visibility = "visible";
        }

    }

    function AddAccount(_showFormButton, _createAccountDiv, _createAccountForm, _submitButton, _closeButton, _errorMessageDiv){
        this.showFormButton = _showFormButton;
        this.createAccountDiv = _createAccountDiv;
        this.createAccountForm = _createAccountForm;
        this.submitButton = _submitButton;
        this.closeButton = _closeButton;
        this.errorMessageDiv = _errorMessageDiv;
        let self = this;

        this.addAccount = () => {
            if(self.createAccountForm.checkValidity()){
                makeCall("POST","CreateAccount", self.createAccountForm,(req) => {
                    if(req.readyState === XMLHttpRequest.DONE) {
                        let messageStr = req.responseText;
                        if(req.status !== 200){
                            self.errorMessageDiv.style.visibility = "visible";
                            self.errorMessageDiv.textContent = messageStr;
                        }
                        else{
                            self.errorMessageDiv.style.visibility = "hidden";
                            let click = new Event("click");
                            self.closeButton.dispatchEvent(click);
                            pageOrchestrator.refresh();
                        }
                    }
                });
            }
        }

        this.registerEvents = () => {
            this.showFormButton.addEventListener("click",(e) => {
                self.createAccountDiv.className = "transfer_msg col-md-5 mb-5 container-fluid";
                messageContainer.className = "messageContainer";
            },false);

            this.closeButton.addEventListener("click", (e) => {
                self.createAccountDiv.className += " hidden";
                messageContainer.className += " hidden";
                self.createAccountForm.reset();
            }, false);

            this.submitButton.addEventListener("click", () => self.addAccount(), false);
        }
    }

    function AccountDetails(_emptySelectionDiv, _selectionMessageDiv, _accountSelectedDiv, _accountIDSpan, _accountName, _accountBalance,
                            _transferList){
        this.emptySelectiondiv = _emptySelectionDiv;
        this.selectionMessageDiv = _selectionMessageDiv;
        this.accountSelectedDiv = _accountSelectedDiv;
        this.accountIDSpan = _accountIDSpan;
        this.accountName = _accountName;
        this.accountBalance = _accountBalance;
        this.transferList = _transferList;

        let self = this;

        // Resets the AccountDetails view to "No account selected" status.
        this.reset = () => {
            this.accountSelectedDiv.className = "hidden";
            this.emptySelectiondiv.className = "";
        }

        // Fetch data for given AccountID
        this.show = (accountID) => {
            makeCall("GET", "SelectAccount?bankAccountID=" + accountID, null,
                (req) => {
                    if(req.readyState === XMLHttpRequest.DONE) {
                        let message = req.responseText;
                        if(req.status === 200) { //OK
                            let account = JSON.parse(message);
                            self.update(account);
                            self.emptySelectiondiv.className = "hidden";
                            self.accountSelectedDiv.className = "";
                        } else {
                            // GET Unsuccessful, show error
                            self.selectionMessageDiv.innerHTML = "";
                            self.selectionMessageDiv.textContent = message;

                            self.selectionMessageDiv.style.visibility = "visible";
                        }
                    }
            });
        }

        this.update = (data) => {
            //Clear previous transfers
            self.accountSelectedDiv.className = "";

            //Set main account info
            self.accountName.textContent = data.account.name;
            self.accountIDSpan.textContent = data.account.ID;
            self.accountBalance.textContent = data.account.balance;

            self.transferList.innerHTML = "";
            //Build transfer list
            let transfers = data.transfers;
            transfers.forEach( (transfer) => {
                let transferDiv = document.createElement("div");
                transferDiv.className = "row transfer mb-3";

                // UPPER ROW
                let upperRow = document.createElement("div");
                upperRow.className = "row transfer_up";

                //Sender ID
                let left_data_UP = document.createElement("span");
                left_data_UP.className = "col-md-4 left_data";
                left_data_UP.textContent = "Sender ID: " + transfer.senderID;

                let spacer_UP = document.createElement("div");
                spacer_UP.className = "col-md-4";

                //Transfer reason
                let right_data_UP = document.createElement("span");
                right_data_UP.className = "col-md-4 right_data";
                right_data_UP.textContent = transfer.reason;

                //Build upper row
                upperRow.appendChild(left_data_UP);
                upperRow.appendChild(spacer_UP);
                upperRow.appendChild(right_data_UP);

                // LOWER ROW
                let lowerRow = document.createElement("div");
                lowerRow.className = "row transfer_down";

                //Timestamp
                let left_data_LOW = document.createElement("span");
                left_data_LOW.className = "col-md-4 left_data";
                left_data_LOW.textContent = "Timestamp: " + transfer.timestamp;

                let spacer_LOW = document.createElement("div");
                spacer_LOW.className = "col-md-4";

                //Transfer amount
                let right_data_LOW = document.createElement("span");
                right_data_LOW.className = "col-md-4 right_data";
                right_data_LOW.textContent = (transfer.senderID === userDetails.ID ? "-" : "+").concat(transfer.amount);

                //Build lower row
                lowerRow.appendChild(left_data_LOW);
                lowerRow.appendChild(spacer_LOW);
                lowerRow.appendChild(right_data_LOW);

                //Build transfer box and append it to list
                transferDiv.appendChild(upperRow);
                transferDiv.appendChild(lowerRow);
                self.transferList.appendChild(transferDiv);
            });
        }


    }

    function TransferForm(_transferForm, _transferButton){
        this.transferForm = _transferForm;
        this.inputRecipientUserID = this.transferForm.elements["recipientID"];
        this.inputRecipientAccountID = this.transferForm.elements["recAccountID"];
        this.transferButton = _transferButton;
        let self = this;

        this.registerEvents = () => {
            this.transferButton.addEventListener("click",(e) => {
                if(self.createAccountForm.checkValidity()){
                    makeCall("POST","MakeTransfer", self.transferForm,(req) => {
                        if(req.readyState === XMLHttpRequest.DONE) {
                            let messageStr = req.responseText;
                            if(req.status === 200){
                                pageOrchestrator.transferResult.update(true,messageStr);
                                pageOrchestrator.refresh(messageStr);
                            }
                            else if(req.status === 403){
                                pageOrchestrator.transferResult.update(false,messageStr);
                                pageOrchestrator.refresh(messageStr);
                            }
                            else{
                                self.errorMessageDiv.className = "";
                                self.errorMessageDiv.textContent = messageStr;
                            }
                        }
                    });
                }
            },false);

            /*
            Adds the listeners to the input fields which have autocomplete
             */
            this.inputRecipientUserID.addEventListener("focus", (e) => {
                contacts.autocompleteRecipientUserID(e.target.value);
            }, false);

            this.inputRecipientUserID.addEventListener("keyup", (e) => {
                contacts.autocompleteRecipientUserID(e.target.value);
            }, false);

            this.inputRecipientAccountID.addEventListener("focus", (e) => {
                contacts.autocompleteRecipientAccountID(e.target.value);
            }, false);

            this.inputRecipientAccountID.addEventListener("keyup", (e) => {
                contacts.autocompleteRecipientAccountID(e.target.value);
            }, false);

        }


    }

    function TransferResult(_transferBox, _transferAcceptedBox, _transferFailedBox, _transferStatus, _transferSender,
                            _transferRecipient, _transferAmount, _transferReason, _failDetails, _closeBoxButton,
                            _addContactButton, _addContactStatus) {
        this.transferBox = _transferBox;
        this.transferAcceptedBox = _transferAcceptedBox;
        this.transferFailedBox = _transferFailedBox;
        this.transferStatus = _transferStatus;
        //TransferParticipants
        this.transferSender = _transferSender;
        this.transferRecipient = _transferRecipient;

        this.transferAmount = _transferAmount;
        this.transferReason = _transferReason;
        this.closeBoxButton = _closeBoxButton;
        this.addContactButton = _addContactButton;
        this.addContactStatus = _addContactStatus;
        let self = this;

        this.reset = () => {
            self.transferBox.className = "hidden";
            messageContainer.className += "hidden";
        }

        //assigns the behaviour to close button
        this.registerEvents = () => {
            //assigns behaviour to close button
            self.closeBoxButton.addEventListener("click", (e) => {
                self.reset();
            }, false);
            //Assigns behaviour to addContact button
            self.addContactButton.addEventListener("click", (e) => {
                //Create form data with the accountID to add
                let addContactForm = new FormData();
                addContactForm.append("contactID", self.transferRecipient.accountID.textContent);
                makeCall("POST","AddContact", addContactForm,(req) => {
                    if(req.readyState === XMLHttpRequest.DONE) {
                        let messageStr = req.responseText;
                        if(req.status !== 200){
                            self.errorMessageDiv.className = "";
                            self.errorMessageDiv.textContent = messageStr;
                        }
                        else{
                            self.addContactButton.className += " hidden"
                            contacts.load();
                        }
                    }
                });
            }, false);
        }

        this.update = (success,data) => {
            if(success){
                self.transferStatus = "Transfer Confirmed";
                self.transferBox.innerHTML = "";
                self.transferFailedBox.className = "hidden";
                self.transferAcceptedBox.className = "";
                //sets the content of the sender account div
                self.transferSender.accountID.textContent = data.sender.ID;
                self.transferSender.accountName.textContent = data.sender.name;
                self.transferSender.oldBalance.textContent = data.sender.balance;
                self.transferSender.newBalance.textContent = data.sender.newBal;

                //sets the content of the recipient account div
                self.transferRecipient.accountID.textContent = data.recipient.ID;
                self.transferRecipient.userID.textContent = data.recipient.userID;
                self.transferRecipient.accountName.textContent = data.recipient.name;
                self.transferRecipient.oldBalance.textContent = data.recipient.balance;
                self.transferRecipient.newBalance.textContent = data.recipient.newBal;

                //sets the content of the transfer div
                self.transferAmount.textContent = data.transfer.amount;
                self.transferReason.textContent = data.transfer.reason;

                //if not contact, show button, otherwise hide it
                self.addContactButton.className = "col-md-3 btn blue-button accepted_btn";
                if(contacts.isContact(data.recipient.userID, data.recipient.ID)) {
                    self.addContactButton.className += " hidden";
                }
            }
            else{
                self.transferStatus = "Transfer Failed";
                self.transferFailedBox.className = "";
                self.transferAcceptedBox.className = "hidden";

                //sets the content div
                self.failDetails.textContent = data;
            }
            messageContainer.className = "messageContainer";
        }
    }

    function Contacts(_datalistRecipientUserIDs, _datalistRecipientAccountIDs) {
        this.contactsMap = null;
        this.datalistRecipientUserIDs = _datalistRecipientUserIDs;
        this.datalistRecipientAccountIDs = _datalistRecipientAccountIDs;

        this.load = () => {
            let self = this;
            makeCall("GET", "GetContacts", null, (req) => {
                if(req.readyState === XMLHttpRequest.DONE) {
                    if(req.status !== 200) {
                        alert(req.responseText);
                    } else {
                        self.contactsMap = new Map(Object.entries(JSON.parse(req.responseText).contacts));
                        //TODO other stuff?
                    }

                }
            }, false);
        }

        this.isContact = (userID, accountID) => {
            //Do not allow user to add himself as contact
            if(userID === userDetails.ID)
                return true;

            let accountList = this.contactsMap.get(userID);
            if(accountList === undefined)
                return false;

            let result = false;
            accountList.forEach( (contactID) => {
               if(accountID === contactID)
                   result = true;
            });

            return result;
        }

        this.autocompleteRecipientUserID = (recipientUserID) => {
            //Clear suggestions
            this.datalistRecipientUserIDs.innerHTML = "";
            this.datalistRecipientAccountIDs.innerHTML = "";

            //Gets the recipient IDs in contacts
            let recipientUserIDsList = Object.keys(this.contactsMap);

            /*
            If the param given isn't a contact shows suggestions
            Adds the userID to suggestions if it starts with the param given
            so if the input box is only focused and no key has been pressed all the userID contained in
            recipientUserIDsList will be added
             */
            if(!recipientUserIDsList.contains(recipientUserID)){
                //partial suggestions
                let suggestedRecipientUserIDs = [];
                recipientUserIDsList.forEach(userID => {
                    if(String(userID).startsWith(recipientUserID)){
                        suggestedRecipientUserIDs.push(userID);
                    }
                });
                suggestedRecipientUserIDs.forEach(userID => {
                    let option = document.createElement("option");
                    option.text = userID;
                    option.value = userID;
                    this.datalistRecipientUserIDs.appendChild(option);
                });

            }
        }

        this.autocompleteRecipientAccountID = (recipientUserID, recipientAccountID, currentAccountID) => {
            //Clear suggestions
            this.datalistRecipientUserIDs.innerHTML = "";
            this.datalistRecipientAccountIDs.innerHTML = "";

            //Gets the userIDs in contacts to check if the recipient user ID is already matched
            let recipientUserIDsList = Object.keys(this.contactsMap);

            if(recipientUserIDsList.contains(recipientUserID)){ //Checks if the recipient user ID is already matched
                //Suggests the accounts of the user selected
                let recipientAccountIDsList = this.contactsMap[recipientUserID];

                /*
                If the param recipientAccountID isn't a contact shows suggestions
                Adds the userID to suggestions if it starts with the param given
                so if the input box is only focused and no key has been pressed all the userID contained in
                recipientUserIDsList will be added
                 */
                if(!recipientAccountIDsList.contains(recipientAccountID)){
                    let suggestedRecipientAccountIDs = [];
                    recipientAccountIDsList.forEach(accountID => {
                        if(String(accountID).startsWith(recipientAccountID) && accountID != currentAccountID){ //Checks if senderID != recipientID
                            suggestedRecipientAccountIDs.push(accountID);
                        }
                    });
                    suggestedRecipientAccountIDs.forEach(accountID => {
                        let option = document.createElement("option");
                        option.text = accountID;
                        option.value = accountID;
                    });
                }
            }

        }
    }

    function TransferParticipant(_userID, _accountID, _accountName, _oldBalance, _newBalance) {
        this.userID = _userID;
        this.accountID = _accountID;
        this.accountName = _accountName;
        this.oldBalance = _oldBalance;
        this.newBalance = _newBalance;
    }

    function PageOrchestrator() {
        //SIDEBAR elements
        let msg = "message_container";

        this.start = () => {
            userDetails = new UserDetails(
                sessionStorage.getItem("id"),
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("user_welcome"),
                document.getElementById("personalID")
            );
            userDetails.show();

            accountList = new AccountList(
                document.getElementById("cashSum"),
                document.getElementById("accountList"),
                document.getElementById("alertBox")
            );
            accountList.show();

            addAccount = new AddAccount(
                document.getElementById("addAccountBtn"),
                document.getElementById("create_acc_div"),
                document.getElementById("create_acc_form"),
                document.getElementById("submitCreationBTN"),
                document.getElementById("closeCreationBoxBTN"),
                document.getElementById("creation_message_div")
            );
            addAccount.registerEvents();

            accountDetails = new AccountDetails(
                document.getElementById("noAccSelected"),
                document.getElementById("selection_message_div"),
                document.getElementById("accSelected"),
                document.getElementById("accID"),
                document.getElementById("accName"),
                document.getElementById("accBal"),
                document.getElementById("transferList")
            );
            accountDetails.reset();

            transferForm = new TransferForm(
                document.getElementById("makeTransferForm"),
                document.getElementById("requestTransferBtn")
            );
            transferForm.registerEvents()

            let transferSender = new TransferParticipant(
                document.getElementById("senderID"),
                document.getElementById("sndAccID"),
                document.getElementById("sndAccName"),
                document.getElementById("oldBalSND"),
                document.getElementById("newBalSND")
            );
            let transferRecipient = new TransferParticipant(
                document.getElementById("recipientID"),
                document.getElementById("recAccID"),
                document.getElementById("recAccName"),
                document.getElementById("oldBalREC"),
                document.getElementById("newBalREC")
            );
            transferResult = new TransferResult(
                document.getElementById("transferBox"),
                document.getElementById("transferAccepted"),
                document.getElementById("transferFailed"),
                document.getElementById("transferStatus"),
                transferSender,
                transferRecipient,
                document.getElementById("transferAMT"),
                document.getElementById("transferReason"),
                document.getElementById("transfer_details"),
                document.getElementById("closeBoxBTN"),
                document.getElementById("addContactBTN"),
                document.getElementById("addContactStatus")
            );
            transferResult.registerEvents();

            contacts = new Contacts(
                document.getElementById("recipientUserIDs"),
                document.getElementById("recipientAccountIDs")
            );
            contacts.load();

            messageContainer = document.getElementById("message_container");
        }

        this.refresh = (currentAccount) => {
            if(currentAccount === undefined) {
                accountDetails.reset();
            } else {
                accountDetails.accountID = currentAccount;
                accountDetails.show();
            }
            accountList.reset();
            accountList.show();
        }
    }
}