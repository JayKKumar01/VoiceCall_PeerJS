//local-video
let peer
let localStream
let remoteStream

function init(userId) {
    peer = new Peer(userId, {
        port: 443,
        path: '/',
    })

    peer.on('open', () => {
        Android.onPeerConnected()
    })

    listen()
}



function test(){
const audio = document.getElementById('audioPlayer')
audio.play()
}

function listen() {
    peer.on('call', (call) => {
        navigator.mediaDevices.getUserMedia({
            audio: {
                        echoCancellation: true,
                        echoCancellationType: { ideal: "system" },
                        channelCount: 1,
                        sampleRate: 48000,
                        noiseSuppression: true,
                        autoGainControl: true,
                        latency: 0.003
                    },
                    video: false,
        }).then((stream) => {
            localStream = stream
            call.answer(stream)
            call.on('stream', (remoteStream) => {
                createAudioElement(remoteStream)
            })
        }).catch((error) => {
            console.error('Error accessing media devices:', error)
        })
    })
}

function createAudioElement(remoteStream) {
    const audioContainer = document.getElementById('box')
    const audioElement = document.createElement('audio')
    audioElement.autoplay = true
//    audioElement.controls = true
    audioContainer.appendChild(audioElement)
    audioElement.srcObject = remoteStream
    audioElement.play()
}

function startCall(otherUserIds) {
    navigator.mediaDevices.getUserMedia({
        audio: {
            echoCancellation: true,
            echoCancellationType: { ideal: "system" },
            channelCount: 1,
            sampleRate: 48000,
            noiseSuppression: true,
            autoGainControl: true,
            latency: 0.003
        },
        video: false,
    }).then((stream) => {
        localStream = stream;
        otherUserIds.forEach((otherUserId) => {
            const call = peer.call(otherUserId, stream);
            call.on('stream', (remoteStream) => {
                createAudioElement(remoteStream);
            });
        });
    }).catch((error) => {
        console.error('Error accessing media devices:', error);
    });

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

function endCall() {
    peer.disconnect()
}
