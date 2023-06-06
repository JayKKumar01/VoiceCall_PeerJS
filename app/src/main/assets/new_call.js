let remoteVideo = document.getElementById("remote-video")


let peer;
let localStream;
const connectedPeers = {};

function init(userId) {
  peer = new Peer(userId, {
    port: 443,
    path: '/'
  });

  peer.on('open', () => {
    Android.onPeerConnected();
  });

  listen();
}


function listen() {
  peer.on('call', (call) => {
    navigator.getUserMedia(
      {
        audio: true,
        video: false
      },
      (stream) => {
        localStream = stream;

        const peerId = call.peer;
        connectedPeers[peerId] = call;

        call.answer(stream);
        call.on('stream', (remoteStream) => {
          // Handle the incoming audio stream
        });
      }
    );
  });
}



function startCall(otherUserIds) {
  navigator.getUserMedia(
    {
      audio: true,
      video: false
    },
    (stream) => {
      localStream = stream;

      otherUserIds.forEach((otherUserId) => {
        const call = peer.call(otherUserId, stream);
        connectedPeers[otherUserId] = call;

        call.on('stream', (remoteStream) => {
          // Handle the remote audio stream
        });
      });
    }
  );
}
function startCall(otherUserId) {
    navigator.getUserMedia({
        audio: {
            mandatory: {
            echoCancellation: false,
            noiseSuppression: true,
            googAutoGainControl: true,
            googNoiseSuppression: true,
            googHighpassFilter: true,
            googTypingNoiseDetection: true
            },
            optional: []
        }
    }, (stream) => {
        localStream = stream


        const call = peer.call(otherUserId, stream)
        call.on('stream', (remoteStream) => {
            remoteVideo.srcObject = remoteStream

        })

    })
}
function toggleAudio(b) {
  const audioTracks = localStream.getAudioTracks();
  audioTracks.forEach((track) => {
    track.enabled = (b === "true");
  });
}

function endCall() {
  Object.values(connectedPeers).forEach((call) => {
    call.close();
  });
  connectedPeers = {};
}
////////










function toggleVideo(b) {
    if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }
} 

function toggleAudio(b) {
    if (b == "true") {
        localStream.getAudioTracks()[0].enabled = true
    } else {
        localStream.getAudioTracks()[0].enabled = false
    }
} 