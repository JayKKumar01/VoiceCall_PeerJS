let remoteVideo = document.getElementById("remote-video")

let peer
function init(userId) {
    peer = new Peer(userId, {

        port: 443,
        path: '/'
    })

    peer.on('open', () => {
        Android.onPeerConnected()
    })

    listen()
}



let localStream
function listen() {
    peer.on('call', (call) => {

        navigator.mediaDevices.getDisplayMedia({
        video: {
        cursor: "always"
        }

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


            call.answer(stream)
            call.on('stream', (remoteStream) => {
                remoteVideo.srcObject = remoteStream
            })

        })
        
    })
}

function startCall(otherUserId) {
    navigator.mediaDevices.getDisplayMedia({
            video: {
            cursor: "always"
            }
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