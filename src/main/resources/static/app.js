const tokenForm =
    document.getElementById("token-form");

const tokenInput =
    document.getElementById("token-input");

const chatForm =
    document.getElementById("chat-form");

const messageInput =
    document.getElementById("message-input");

const messages =
    document.getElementById("messages");

const sendButton =
    document.getElementById("send-button");

const connectionStatus =
    document.getElementById("connection-status");

const errorMessage =
    document.getElementById("error-message");


let accessToken = "";


/*
 * Development-only token loading.
 *
 * We intentionally keep the JWT only in JavaScript memory.
 * We do NOT put it in localStorage.
 */
tokenForm.addEventListener("submit", event => {

    event.preventDefault();

    const token =
        tokenInput.value.trim();

    if (!token) {
        showError("Please paste a JWT access token.");
        return;
    }

    accessToken = token;

    // Remove token from the visible input once loaded.
    tokenInput.value = "";

    connectionStatus.textContent = "Token loaded";

    connectionStatus.classList.remove(
        "status-disconnected"
    );

    connectionStatus.classList.add(
        "status-connected"
    );

    hideError();

    messageInput.focus();
});


/*
 * Main chat submission.
 */
chatForm.addEventListener("submit", async event => {

    event.preventDefault();

    hideError();

    if (!accessToken) {

        showError(
            "Load a Keycloak access token before sending messages."
        );

        return;
    }

    const message =
        messageInput.value.trim();

    if (!message) {
        return;
    }

    addMessage("You", message, "user");

    messageInput.value = "";

    setLoading(true);

    try {

        const response = await fetch("/api/chat", {

            method: "POST",

            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${accessToken}`
            },

            body: JSON.stringify({
                message: message
            })

        });


        if (!response.ok) {

            await handleHttpError(response);

            return;
        }


        const data =
            await response.json();


        addMessage(
            "AW Assistant",
            data.answer,
            "assistant"
        );

    }
    catch (error) {

        console.error(error);

        showError(
            "Unable to reach the server. Check that Spring Boot is running."
        );

    }
    finally {

        setLoading(false);

        messageInput.focus();

    }

});


/*
 * Send with Enter.
 * Shift + Enter creates a new line.
 */
messageInput.addEventListener("keydown", event => {

    if (
        event.key === "Enter"
        && !event.shiftKey
    ) {

        event.preventDefault();

        chatForm.requestSubmit();

    }

});


function addMessage(sender, content, type) {

    const message =
        document.createElement("div");

    message.classList.add(
        "message",
        `${type}-message`
    );


    const label =
        document.createElement("div");

    label.classList.add("message-label");

    label.textContent = sender;


    const messageContent =
        document.createElement("div");

    messageContent.classList.add(
        "message-content"
    );

    /*
     * IMPORTANT:
     * Use textContent rather than innerHTML.
     *
     * Model output is untrusted content.
     */
    messageContent.textContent = content;


    message.appendChild(label);

    message.appendChild(messageContent);

    messages.appendChild(message);


    message.scrollIntoView({
        behavior: "smooth",
        block: "end"
    });

}


async function handleHttpError(response) {

    let detail = "";

    try {

        const body =
            await response.json();

        detail =
            body.detail
            || body.title
            || "";

    }
    catch {
        // Ignore JSON parsing failure.
    }


    switch (response.status) {

        case 400:
            showError(
                detail || "The request is invalid."
            );
            break;

        case 401:

            accessToken = "";

            connectionStatus.textContent =
                "Token required";

            connectionStatus.classList.remove(
                "status-connected"
            );

            connectionStatus.classList.add(
                "status-disconnected"
            );

            showError(
                "Your token is missing, invalid, or expired. Load a new token."
            );

            break;

        case 403:

            showError(
                "Authenticated, but your user does not have CHATBOT_USE."
            );

            break;

        default:

            showError(
                detail
                || `Server returned HTTP ${response.status}.`
            );

    }

}


function setLoading(loading) {

    sendButton.disabled = loading;

    messageInput.disabled = loading;

    sendButton.textContent =
        loading
            ? "Thinking..."
            : "Send";

}


function showError(message) {

    errorMessage.textContent = message;

    errorMessage.classList.remove(
        "hidden"
    );

}


function hideError() {

    errorMessage.textContent = "";

    errorMessage.classList.add(
        "hidden"
    );

}