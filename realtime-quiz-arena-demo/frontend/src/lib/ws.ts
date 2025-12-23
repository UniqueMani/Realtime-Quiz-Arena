import { Client } from '@stomp/stompjs'

export function connectStomp(onConnected: (client: Client) => void) {
    const wsUrl =
        (location.protocol === 'https:' ? 'wss://' : 'ws://') +
        location.host +
        '/ws'

    const client = new Client({
        brokerURL: wsUrl,
        reconnectDelay: 2000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: () => {},
        onConnect: () => onConnected(client),
        onStompError: (frame) => {
            console.error('STOMP error:', frame.headers['message'], frame.body)
        },
        onWebSocketError: (evt) => {
            console.error('WebSocket error:', evt)
        },
    })

    client.activate()
    return client
}
