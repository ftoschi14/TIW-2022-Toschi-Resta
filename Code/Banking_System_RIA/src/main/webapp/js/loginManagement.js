{ //Avoid variables ending up in the global scope
    //Page components
    let wizard,
        pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        if(sessionStorage.getItem("id") !== null &&
            sessionStorage.getItem("name") !== null &&
            sessionStorage.getItem("surname") !== null) {
            window.location.href = "Home.html";
        } else {
            pageOrchestrator.start();
        }
    }, false);

    /**
     *  (1) Fetch login/register form
     *  (2) Create Login Submit button + Go To register button
     *  (3) Show login form
     *
     * @constructor
     */
    function PageOrchestrator() {
        var login_box = document.getElementById("login_box");
        var login_container = document.getElementById("login_container");
        var registration_container = document.getElementById("registration_container");
        var login_form = document.getElementById("login_form");
        var register_form = document.getElementById("register_form");
        var submit_div = document.getElementById("login_button_div");
        var change_form_div = document.getElementById("register_button_div");
        var message_div = document.getElementById("message_div");

        this.start = () => {
            wizard = new Wizard(submit_div, change_form_div, login_container, registration_container, login_form, register_form, message_div);
            wizard.preventRefresh(); //Prevents forms from incorrectly refreshing the page (submit event)
            wizard.show(true);

            registerImplicitSubmission(login_box);
        }
    }

    function Wizard(_submitDiv, _changeFormDiv, _loginContainer, _registrationContainer, _loginForm, _registerForm, _messageDiv) {
        this.submitDiv = _submitDiv;
        this.changeFormDiv = _changeFormDiv;
        this.loginContainer = _loginContainer;
        this.registrationContainer = _registrationContainer;
        this.loginForm = _loginForm;
        this.registerForm = _registerForm;
        this.messageDiv = _messageDiv;

        this.passwordInput = this.registerForm.querySelector('input[name="password"]');
        this.repeatPasswordInput = this.registerForm.querySelector('input[name="passwordRep"]');

        this.show = (isLogin) => {
            var self = this;
            let submit_button, changeform_button;
            this.submitDiv.innerHTML = ""; // Clear login button div
            this.changeFormDiv.innerHTML = ""; // Clear register button div
            /*
             * Button creation
             */
            submit_button = document.createElement("button");
            submit_button.className = "btn blue-button login";
            submit_button.id = "submit_button";
            changeform_button = document.createElement("button");
            changeform_button.className = "btn blue-button register";
            submit_button.textContent = "Submit";

            //Register event
            if(isLogin === true) {
                // Register events for new submit button (Submit login form)
                submit_button.addEventListener("click",(e) => {
                    //Form validity check
                    if(self.loginForm.checkValidity()){
                        //POST to Login servlet
                        makeCall("POST", 'Login', self.loginForm, (req) => {
                            if(req.readyState === XMLHttpRequest.DONE){
                                let messageStr = req.responseText;
                                self.messageDiv.innerHTML = "";
                                if(req.status !== 200){
                                    self.messageDiv.className = "col-md-12 alert alert-warning";
                                    self.messageDiv.textContent = messageStr;
                                    self.messageDiv.style.visibility = "visible";
                                }
                                else{
                                    var respStr = JSON.parse(req.responseText);
                                    sessionStorage.setItem("id", respStr.ID);
                                    sessionStorage.setItem("name", respStr.name);
                                    sessionStorage.setItem("surname", respStr.surname);
                                    window.location.href = "Home.html";
                                }

                            }
                        }, false);
                    }

                },false);

                //defines the behaviour of the changeform_button
                changeform_button.textContent = "Register";
                changeform_button.addEventListener("click" , (e) => {
                    self.show(false);
                },false);

                //shows the login form
                this.registrationContainer.className = "hidden";
                this.loginContainer.className = "";

            } else {
                // Register events for new submit button (Submit registration form)
                submit_button.addEventListener("click", (e) => {
                    //First form validity check
                    if(registerForm.checkValidity() === true) {
                        //Check if passwords are matching
                        if(self.passwordInput.value !== self.repeatPasswordInput.value) {
                            self.messageDiv.innerHTML = "";
                            self.messageDiv.className = "col-md-12 alert alert-warning";
                            self.messageDiv.textContent = "Passwords do not match!";
                            self.messageDiv.style.visibility = "visible";
                            return;
                        }
                        //POST to Register servlet
                        makeCall("POST", "Register", self.registerForm, (req) => {
                            if(req.readyState === 4) {
                                // Get response and create message element
                                let messageStr = req.responseText;

                                // Clear message div and append new message
                                self.messageDiv.innerHTML = "";
                                self.messageDiv.textContent = messageStr;

                                if(req.status !== 200) { // Registration was unsuccessful, show error message
                                    self.messageDiv.className = "col-md-12 alert alert-warning";

                                } else { // Registration successful, show success message, clear form and go back to login form
                                    self.messageDiv.className = "col-md-12 alert alert-primary";

                                    self.reset();
                                    self.show(true);
                                }

                                self.messageDiv.style.visibility = "visible";
                            }
                        }, false);
                    }
                });
                //Define ChangeForm button behaviour
                changeform_button.textContent = "Login";
                changeform_button.addEventListener("click", (e) => {
                    self.show(true);
                }, false);

                this.registrationContainer.className = "";
                this.loginContainer.className = "hidden";
            }
            this.submitDiv.appendChild(submit_button);
            this.changeFormDiv.appendChild(changeform_button);
        }

        this.reset = () => {
            this.registerForm.reset();
            this.loginForm.reset();
        }

        this.preventRefresh = () => {
            this.registerForm.addEventListener("submit", (e) => {
                e.preventDefault();
                e.stopPropagation();
            });
            this.loginForm.addEventListener("submit", (e) => {
                e.preventDefault();
                e.stopPropagation();
            });
        }
    }

    function registerImplicitSubmission(container) {
        container.addEventListener('keydown', (e) => {
            if(e.key === "Enter") {
                e.preventDefault();
                let button = document.getElementById("submit_button");
                let click = new Event("click");
                button.dispatchEvent(click);
            }
        });
    }
}