const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8666/gs-guide-websocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/greetings', (greeting) => {
        showGreeting(JSON.parse(greeting.body).content);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendText() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'text': $("#text").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

// Connect to a topic
function connectToTopic(topic) {
    const destination = "/topic/" + topic;

    stompClient.subscribe(destination, (greeting) => {
        showGreeting(JSON.parse(greeting.body).content);
    });
}

// Connect to a private chat
function connectToPrivateChat(topic, username) {
    const destination = "/private/" + topic + "/" + username;

    stompClient.subscribe(destination, (greeting) => {
        showGreeting(JSON.parse(greeting.body).content);
    });
}

// Send text to a topic or chat
function sendTextTo(topic, text, username = null) {
    const destination = username ? `/app/chat/${topic}/${username}` : `/app/hello`;

    stompClient.publish({
        destination: destination,
        body: JSON.stringify({ 'text': text })
    });
}

// Connect to a topic and send a message
connectToTopic("sports");
sendTextTo("sports", "Hey, any updates on the game?");

// Connect to a private chat and send a message
connectToPrivateChat("private-topic", "Alice");
sendTextTo("private-topic", "Hello Alice, how are you?", "Bob");

$(function () {
    $("form").on('submit', (e) => e.preventDefault());

    $("#connect").click(() => {
        const selectedTopic = $("#topicSelect").val();
        connectToTopic(selectedTopic);
        connect();
    });

    $("#connectPrivate").click(() => {
        const selectedTopic = $("#topicSelect").val();
        const username = $("#privateUsername").val();
        connectToPrivateChat(selectedTopic, username);
        connect();
    });

    $("#disconnect").click(() => disconnect());
    $("#send").click(() => {
        const text = $("#text").val();
        const selectedTopic = $("#topicSelect").val();
        const username = $("#privateUsername").val();

        if (username) {
            sendTextTo(selectedTopic, text, username);
        } else {
            sendTextTo(selectedTopic, text);
        }
    });
});


// $(function () {
//     $("form").on('submit', (e) => e.preventDefault());
//     $( "#connect" ).click(() => connect());
//     $( "#disconnect" ).click(() => disconnect());
//     $( "#send" ).click(() => sendText());
// });