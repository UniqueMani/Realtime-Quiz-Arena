import axios from 'axios'

export const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
})

export type RoomCreateResponse = { roomCode: string; hostToken: string }
export type JoinRoomResponse = { playerId: string; nickname: string }

export type QuestionPush = {
    questionId: number
    stem: string
    options: string[]
    openedAtEpochMs: number
    closedAtEpochMs: number
}

export type LeaderboardEntry = {
    playerId: string
    nickname: string
    totalScore: number
}

export type LeaderboardPush = {
    entries: LeaderboardEntry[]
    serverTimeEpochMs: number
}

export async function createRoom(): Promise<RoomCreateResponse> {
    const res = await api.post('/rooms')
    return res.data
}

export async function joinRoom(code: string, nickname: string): Promise<JoinRoomResponse> {
    const res = await api.post(`/rooms/${code}/join`, { nickname })
    return res.data
}

export async function startRoom(code: string, hostToken: string): Promise<QuestionPush> {
    const res = await api.post(
        `/rooms/${code}/start`,
        null,
        { headers: { 'X-Host-Token': hostToken } }
    )
    return res.data
}
