{// avoid variables ending up in the global scope
    let userDetails, accountList, accountDetails, transferResult, pageOrchestrator = new PageOrchestrator();

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
            personalIDContainer.textContent = this.ID;
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

        this.show = (next) => {
            let self = this;
            makeCall("GET","GetAccounts",null,
                (req) => {
                    if(req.readyState === 4){
                        let message = req.responseText;
                        if(req.status === 200){
                            let accounts = JSON.parse(req.responseText);
                            self.update(accounts);
                            if(next) next();  // show the default element of the list if present (?)
                        }
                        else{
                            self.alertBox.textContent = message;
                        }
                    }
                });
        }

        this.update = (arrayAccounts) => {
            let elem,row,anchor,accountDiv,accountNameDiv,spaceDiv,accountBalanceDiv;
            this.accountListContainer.innerHTML = ""; //empty the div
            //build the updated list
            let self = this;
            arrayAccounts.forEach((account) => {
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
                //TODO -> capire come stampare gli euro
                accountBalanceDiv.textContent = account.balance + "&euro";
                anchor = document.createElement("a");
                accountDiv.appendChild(anchor);
                anchor.setAttribute("accountID",account.ID);
                anchor.addEventListener("click", (e) => {
                    accountDetails.show(e.target.getAttribute("accountID"));
                }, false);
                anchor.href = "#";
                self.accountListContainer.appendChild(accountDiv);
            });
            this.accountListContainer.style.visibility = "visible";
        }

        this.autoclick = (accountID) => {
            let e = new Event("click");
            let selector = "a[accountID='" + accountID + "']";
            if(accountID !== null){
                let anchorToClick = document.querySelector(selector);
                if(anchorToClick){
                    anchorToClick.dispatchEvent(e);
                }
            }
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

        this.showForm = () => {
            self.showFormButton.addEventListener("click",(e) => {
                self.createAccountDiv.className = "";
            },false);
        }

        this.addAccount = () => {
            if(self.createAccountForm.checkValidity()){
                makeCall("POST","CreateAccount", self.createAccountForm,(req) => {
                    if(req.readyState === XMLHttpRequest.DONE) {
                        let messageStr = req.responseText;
                        if(req.status !== 200){
                            self.errorMessageDiv.className = "";
                            self.errorMessageDiv.textContent = messageStr;
                        }
                        else{
                            pageOrchestrator.refresh(messageStr);
                        }
                    }
                });
            }
        }

        this.close = () => {
            self.closeButton.addEventListener("click", (e) => {
                self.createAccountDiv.className = "hidden";
            }, false);
        }
    }

    function AccountDetails(_emptySelectionDiv, _accountSelectedDiv, _accountID, _accountName, _accountBalance,
                            _transferList){
        this.emptySelectiondiv = _emptySelectionDiv;
        this.accountSelectedDiv = _accountSelectedDiv;
        this.accountID = _accountID;
        this.accountName = _accountName;
        this.accountBalance = _accountBalance;
        this.transferList = _transferList;
        let self = this;

        this.reset = () => {
            self.emptySelectiondiv.style.visibility = "hidden"
        }

        this.show = () => {

        }

        this.update = () => {

        }


    }

    function RequestTransfer(_transferForm, _transferButton){
        this.transferForm = _transferForm;
        this.transferButton = _transferButton;
        let self = this;

        this.registerEvent = () => {

        }

        this.makeTransfer = () => {

        }

    }

    function TransferResult(_transferBox, _transferStatus, _transferSender, _transferRecipient, _transferAmount,
                            _transferReason, _closeBoxButton, _addContactButton) {
        this.transferBox = _transferBox;
        this.transferStatus = _transferStatus;
        //TransferParticipants
        this.transferSender = _transferSender;
        this.transferRecipient = _transferRecipient;

        this.transferAmount = _transferAmount;
        this.transferReason = _transferReason;
        this.closeBoxButton = _closeBoxButton;
        this.addContactButton = _addContactButton;

        this.show = () => {

        }

        this.reset = () => {

        }

        this.update = () => {

        }
    }

    function TransferParticipant(_ID, _accountID, _accountName, _oldBalance, _newBalance) {
        this.ID = _ID;
        this.accountID = _accountID;
        this.accountName = _accountName;
        this.oldBalance = _oldBalance;
        this.newBalance = _newBalance;
    }

    function PageOrchestrator() {
        //SIDEBAR elements
        var add_account_button = document.getElementById("addAccountBtn");

        this.start = () => {
            userDetails = new UserDetails(
                sessionStorage.getItem("id"),
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("personalID"),
                document.getElementById("user_welcome")
            );

            accountList = new AccountList(
                document.getElementById("cashSum"),
                document.getElementById("accountList"),
                document.getElementById("alertBox")
            );

            accountDetails = new AccountDetails(
                document.getElementById("noAccSelected"),
                document.getElementById("accSelected"),
                document.getElementById("accID"),
                document.getElementById("accName"),
                document.getElementById("accBal"),
                document.getElementById("transferList"),
                document.getElementById("makeTransferForm"),
                document.getElementById("requestTransferBtn")
            );

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
                document.getElementById("transferStatus"),
                transferSender,
                transferRecipient,
                document.getElementById("transferAMT"),
                document.getElementById("transferReason"),
                document.getElementById("closeBoxBTN"),
                document.getElementById("addContactBTN")
            );

        }

        this.refresh = (currentAccount) => {
            accountDetails.accountID = currentAccount;
        }
    }
}