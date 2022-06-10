/**
 * AJAX call
 */
function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest();

    req.onreadystatechange = () => {
        cback(req)
    };

    req.open(method, url);
    if(formElement == null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
    }

    if(formElement !== null & reset === true) {
        formElement.reset();
    }
}