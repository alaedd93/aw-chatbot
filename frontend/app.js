import Keycloak from "keycloak-js";
import "./styles.css";


const keycloak = new Keycloak({

    url:
        "http://localhost:9090",

    realm:
        "aw-chatbot",

    clientId:
        "aw-chatbot-ui"

});

const loginButton =
    document.getElementById("login-button");

const logoutButton =
    document.getElementById("logout-button");

const userName =
    document.getElementById("user-name");

const authStatus =
    document.getElementById("auth-status");

const chatForm =
    document.getElementById("chat-form");

const messageInput =
    document.getElementById("message-input");

const sendButton =
    document.getElementById("send-button");

const messages =
    document.getElementById("messages");

const errorMessage =
    document.getElementById("error-message");

const CONVERSATION_ID_KEY =
    "aw-chatbot-conversation-id";

const newChatButton =
    document.getElementById("new-chat-button")

newChatButton.addEventListener(
    "click",
    () => {

        conversationId =
            createConversationId();

        resetChatUI();

        messageInput.focus();
    }
);

function resetChatUI() {

    messages.replaceChildren();


    addMessage(
        "AW Assistant",
        "New conversation started. How can I help?",
        "assistant"
    );

}


function createConversationId() {

    const id =
        crypto.randomUUID();

    sessionStorage.setItem(
        CONVERSATION_ID_KEY,
        id
    );

    return id;
}


function getConversationId() {

    const existing =
        sessionStorage.getItem(
            CONVERSATION_ID_KEY
        );

    if (existing) {
        return existing;
    }

    return createConversationId();
}

let conversationId =
    getConversationId();


/*
 * -------------------------------------------------------
 * Keycloak initialization
 * -------------------------------------------------------
 */

async function initializeAuthentication() {

    try {

        const authenticated =
            await keycloak.init({

                /*
                 * Authorization Code + PKCE.
                 *
                 * S256 is also Keycloak's current default,
                 * but we make it explicit for learning.
                 */
                pkceMethod: "S256"

            });


        updateAuthenticationUI(authenticated);

    }
    catch (error) {

        console.error(
            "Keycloak initialization failed",
            error
        );

        showError(
            "Unable to initialize authentication."
        );

    }

}

/*
 * -------------------------------------------------------
 * Login
 * -------------------------------------------------------
 */

loginButton.addEventListener(
    "click",
    async () => {

        await keycloak.login({

            redirectUri:
                `${window.location.origin}/`

        });

    }
);


/*
 * -------------------------------------------------------
 * Logout
 * -------------------------------------------------------
 */

logoutButton.addEventListener(
    "click",
    async () => {

        await keycloak.logout({

            redirectUri:
                `${window.location.origin}/`

        });

    }
);

/*
 * -------------------------------------------------------
 * Chat
 * -------------------------------------------------------
 */

chatForm.addEventListener(
    "submit",
    async event => {

        event.preventDefault();

        hideError();


        if (!keycloak.authenticated) {

            showError(
                "You must sign in before sending a message."
            );

            return;

        }


        const message =
            messageInput.value.trim();


        if (!message) {
            return;
        }


        addMessage(
            "You",
            message,
            "user"
        );


        messageInput.value = "";

        setLoading(true);


        try {

            /*
             * Refresh the token if it expires
             * within the next 30 seconds.
             */
            await keycloak.updateToken(30);



            const response =
                await fetch(
                    "/api/chat",
                    {

                        method:
                            "POST",

                        headers: {

                            "Content-Type":
                                "application/json",

                            "Authorization":
                                `Bearer ${keycloak.token}`

                        },

                        body:
                            JSON.stringify({
                                conversationId: conversationId,
                                message: message
                            })

                    }
                );


            if (!response.ok) {

                await handleHttpError(
                    response
                );

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

            console.error(
                "Chat request failed",
                error
            );


            showError(
                "Unable to complete the request."
            );

        }
        finally {

            setLoading(false);

            messageInput.focus();

        }

    }
);


/*
 * Enter sends.
 * Shift + Enter adds a new line.
 */
messageInput.addEventListener(
    "keydown",
    event => {

        if (
            event.key === "Enter"
            && !event.shiftKey
        ) {

            event.preventDefault();

            chatForm.requestSubmit();

        }

    }
);

/*
 * -------------------------------------------------------
 * Authentication UI
 * -------------------------------------------------------
 */

function updateAuthenticationUI(authenticated) {

    if (!authenticated) {

        loginButton.classList.remove(
            "hidden"
        );

        logoutButton.classList.add(
            "hidden"
        );

        userName.classList.add(
            "hidden"
        );

        newChatButton.classList.add(
            "hidden"
        );


        authStatus.textContent =
            "Sign in to start chatting.";

        authStatus.classList.remove(
            "authenticated"
        );


        setChatEnabled(false);

        return;

    }


    loginButton.classList.add(
        "hidden"
    );

    logoutButton.classList.remove(
        "hidden"
    );

    userName.classList.remove(
        "hidden"
    );

    newChatButton.classList.remove(
        "hidden"
    );


    const username =
        keycloak.tokenParsed
            ?.preferred_username
        ?? "User";


    userName.textContent =
        username;


    /*
     * Client-side role checking is only UX.
     *
     * Spring Security remains authoritative.
     */
    const roles =
        keycloak.realmAccess
            ?.roles
        ?? [];


    const canUseChat =
        roles.includes(
            "CHATBOT_USE"
        );


    if (canUseChat) {

        authStatus.textContent =
            "Authenticated";

        authStatus.classList.add(
            "authenticated"
        );

        setChatEnabled(true);

    }
    else {

        authStatus.textContent =
            "Authenticated, but CHATBOT_USE is missing.";

        showError(
            "Your account does not have permission to use the chatbot."
        );

        setChatEnabled(false);

    }

}

/*
 * -------------------------------------------------------
 * Messages
 * -------------------------------------------------------
 */

function addMessage(
    sender,
    content,
    type
) {

    const message =
        document.createElement("div");


    message.classList.add(
        "message",
        `${type}-message`
    );


    const label =
        document.createElement("div");


    label.classList.add(
        "message-label"
    );


    label.textContent =
        sender;


    const messageContent =
        document.createElement("div");


    messageContent.classList.add(
        "message-content"
    );


    /*
     * Never inject model output using innerHTML.
     */
    messageContent.textContent =
        content;


    message.appendChild(
        label
    );


    message.appendChild(
        messageContent
    );


    messages.appendChild(
        message
    );


    message.scrollIntoView({

        behavior:
            "smooth",

        block:
            "end"

    });

}

/*
 * -------------------------------------------------------
 * HTTP errors
 * -------------------------------------------------------
 */

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
        // Response may not contain JSON.
    }


    switch (response.status) {

        case 400:

            showError(
                detail
                || "Invalid request."
            );

            break;


        case 401:

            showError(
                "Authentication expired. Please sign in again."
            );

            break;


        case 403:

            showError(
                "You do not have CHATBOT_USE permission."
            );

            break;


        default:

            showError(
                detail
                || `Server returned HTTP ${response.status}.`
            );

    }

}

/*
 * -------------------------------------------------------
 * Helpers
 * -------------------------------------------------------
 */

function setChatEnabled(enabled) {

    messageInput.disabled =
        !enabled;

    sendButton.disabled =
        !enabled;


    messageInput.placeholder =
        enabled
            ? "Ask AW Assistant..."
            : "Sign in to start chatting...";

}


function setLoading(loading) {

    messageInput.disabled =
        loading;

    sendButton.disabled =
        loading;


    sendButton.textContent =
        loading
            ? "Thinking..."
            : "Send";

}


function showError(message) {

    errorMessage.textContent =
        message;


    errorMessage.classList.remove(
        "hidden"
    );

}


function hideError() {

    errorMessage.textContent =
        "";


    errorMessage.classList.add(
        "hidden"
    );

}


/*
 * -------------------------------------------------------
 * Start application
 * -------------------------------------------------------
 */

initializeAuthentication();