const source = new EventSource('/sse/events');
const eventsUl = document.getElementById('events');

function logEvent(text) {
    const li = document.createElement('li');
    li.innerText = text;
    eventsUl.appendChild(li);
}

// Default listener
source.addEventListener('message', function (e) {
    logEvent(e.data);
}, false);

// Connection opened listener
source.addEventListener('open', function (_) {
    console.log('open');
}, false);

// Error listener
source.addEventListener('error', function (e) {
    if (e.readyState === EventSource.CLOSED) {
        console.log('closed');
    } else {
        console.log('error');
        console.log(e);
    }
}, false);