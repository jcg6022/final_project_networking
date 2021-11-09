// ==================================================================
// Globals (bad)
// ==================================================================
const socket = new WebSocket('ws://localhost:80')
const $messages = document.getElementById('messages')
const $message = document.getElementById('message')
$message.addEventListener('keyup', (event) => {
  if (event.isTrusted && event.key === 'Enter') {
    sendMessage()
  }
})

window.addEventListener('unload', () => {
  socket.close()
})

window.onload = () => {
  socket.onopen = (event) => {
    console.log(event)
  }

  socket.onmessage = (event) => {
    console.log('==================================================')
    console.log('onmessage()')
    console.log('==================================================')
    appendMessage(event.data)
  }

  socket.onerror = (error) => {
    console.log('==================================================')
    console.log('onerror()')
    console.log('==================================================')
    console.error('WebSocket error observed: ' + error)
  }

  socket.onclose = (closeEvent) => {
    console.log('==================================================')
    console.log('onclose()')
    console.log('==================================================')
    // https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
    console.log('code: ' + closeEvent.code)
    console.log(`reason: ${closeEvent.reason}`)
    console.log('cleanly closed? ' + (closeEvent.wasClean ? 'yes' : 'no'))
  }
}

/**
 * Appends the provided text message to the #messages list.
 *
 * @param {String} message the text message
 * @returns
 */
function appendMessage(message) {
  if (typeof message !== 'string') {
    return
  }

  if (message.length === 0) {
    return
  }

  const $li = document.createElement('li')
  $li.innerHTML = message
  $messages.appendChild($li)
}

/**
 * Send a message to the server.
 */
function sendMessage() {
  if (
    socket.readyState === socket.CLOSED ||
    socket.readyState === socket.CLOSING
  ) {
    console.log('Cannot send message: socket is closed.')
    return
  }
  if ($message.value && $message.value.length > 0) {
    try {
      const message = $message.value.trim()
      socket.send(message)
      appendMessage(message)
      $message.value = ''
    } catch (err) {
      console.error(err)
    }
  }
}
