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
      audio: true,
      video: false
    }).then((stream) => {
      localStream = stream;
      call.answer(stream);
      call.on('stream', (remoteStream) => {
        Android.onStreamStarted();
        createAudioElement(remoteStream, call.peer);
        printIntensity(remoteStream, call.peer)
      });
    }).catch((error) => {
      console.error('Error accessing media devices:', error);
    });
  });
}

function printIntensity(stream, otherUserId) {
  Android.onCallback(otherUserId + ": Started");
  const audioContext = new AudioContext();
  const mediaStreamAudioSourceNode = audioContext.createMediaStreamSource(stream);
  const analyserNode = audioContext.createAnalyser();
  mediaStreamAudioSourceNode.connect(analyserNode);

  const pcmData = new Float32Array(analyserNode.fftSize);

  const onFrame = () => {
    analyserNode.getFloatTimeDomainData(pcmData);
    let sumSquares = 0.0;
    for (const amplitude of pcmData) {
      sumSquares += amplitude * amplitude;
    }
    var val = Math.sqrt(sumSquares / pcmData.length);
    val = Math.round(val * 1000);
    Android.onPrintIntensity(otherUserId, val);
  };

  setInterval(onFrame, 100); // Call onFrame every 100 milliseconds
}


var isMuted = false

function muteAllAudioElements(mute) {
isMuted = mute;
  // Get the <div> element
  var div = document.getElementById('box');

  // Get all audio elements within the <div>
  var audioElements = div.getElementsByTagName('audio');

  // Loop through each audio element and mute/unmute it
  for (var i = 0; i < audioElements.length; i++) {
    audioElements[i].muted = mute;
  }
}



function createAudioElement(remoteStream, otherUserId) {
    const audioContainer = document.getElementById('box')
    const audioElement = document.createElement('audio')
    audioElement.autoplay = true
    audioElement.muted = isMuted;
//    audioElement.controls = true
    audioContainer.appendChild(audioElement)
    audioElement.srcObject = remoteStream
    audioElement.play()
    //printIntensity(audioElement, otherUserId);
}

function startCall(otherUserIds) {
  navigator.mediaDevices.getUserMedia({ audio: true, video: false })
    .then((stream) => {
      localStream = stream;
      otherUserIds.forEach((otherUserId) => {
        const call = peer.call(otherUserId, stream);
        call.on('stream', (remoteStream) => {
          Android.onStreamStarted();
          createAudioElement(remoteStream, otherUserId);
        });
      });
    })
    .catch((error) => {
      console.error('Error accessing media devices:', error);
    });
}












//function printIntensity(stream, otherUserId) {
//Android.onCallback(otherUserId+": Started")
//  const audioContext = new (window.AudioContext || window.webkitAudioContext)();
//  const sourceNode = audioContext.createMediaStreamSource(stream);
//  const analyserNode = audioContext.createAnalyser();
//  analyserNode.fftSize = 2048;
//
//  sourceNode.connect(analyserNode);
//  analyserNode.connect(audioContext.destination);
//
//  const bufferLength = analyserNode.fftSize;
//  const dataArray = new Uint8Array(bufferLength);
//
//  function updateSoundIntensity() {
//    analyserNode.getByteTimeDomainData(dataArray);
//
//    let sum = 0;
//    for (let i = 0; i < bufferLength; i++) {
//      const amplitude = (dataArray[i] - 128) / 128; // Normalize amplitude to range [-1, 1]
//      sum += Math.abs(amplitude);
//    }
//    const averageIntensity = sum / bufferLength;
//
//    Android.onPrintIntensity(otherUserId, averageIntensity);
//
//    requestAnimationFrame(updateSoundIntensity);
//  }
//
//  updateSoundIntensity();
//}



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