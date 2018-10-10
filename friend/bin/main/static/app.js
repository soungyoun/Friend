var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#alarm").html("");
}

function connect() {
	var chatBox = $('.chat_box');
	var socket = new SockJS("/alarm_socket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        alert($("#userid").val());
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + $("#userid").val(), function (chat) {
        	var content = JSON.parse(chat.body);
            chatBox.append('<li> [' + content.userid + ']' + content.message + '</li>')
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendAlarm() {
	//채팅그룹 모든 사람에게 메세지 전송
    stompClient.send("/app/alarmMessage" , {}, JSON.stringify({'roomid': $("#roomid").val(),'clubid': $("#clubid").val(), 'userid': $("#userid").val(), 'message': $("#message").val()}));

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendAlarm(); });
});

