import axios from 'axios'

export const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
})

// 添加请求拦截器，记录所有API请求
api.interceptors.request.use(
    (config) => {
        console.log(`[API请求] ${config.method?.toUpperCase()} ${config.url}`, {
            params: config.params,
            data: config.data,
            headers: config.headers,
        })
        return config
    },
    (error) => {
        console.error('[API请求错误]', error)
        return Promise.reject(error)
    }
)

// 添加响应拦截器，记录所有API响应
api.interceptors.response.use(
    (response) => {
        console.log(`[API响应成功] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
            status: response.status,
            data: response.data,
        })
        return response
    },
    (error) => {
        console.error(`[API响应错误] ${error.config?.method?.toUpperCase()} ${error.config?.url}`, {
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            message: error.message,
        })
        return Promise.reject(error)
    }
)

export type RoomCreateResponse = { roomCode: string; hostToken: string }
export type JoinRoomResponse = { playerId: string; nickname: string }

export type QuestionPush = {
    questionId: number
    stem: string
    options: string[]
    openedAtEpochMs: number
    closedAtEpochMs: number
    currentIndex?: number
    totalCount?: number
}

export type QuestionResponse = {
    id: number
    stem: string
    options: string[]
    explanation: string
    category: string
    createdAt: string
    updatedAt: string
}

export type QuestionWithAnswer = QuestionResponse & {
    correctAnswer: string
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
    console.log('[createRoom] 开始创建房间')
    try {
        const res = await api.post('/rooms')
        console.log('[createRoom] 房间创建成功', { data: res.data })
        return res.data
    } catch (error: any) {
        console.error('[createRoom] 房间创建失败', {
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            message: error.message,
        })
        throw error
    }
}

export async function joinRoom(code: string, nickname: string): Promise<JoinRoomResponse> {
    console.log('[joinRoom] 开始加入房间', { code, nickname })
    try {
        const res = await api.post(`/rooms/${code}/join`, { nickname })
        console.log('[joinRoom] 加入房间成功', { data: res.data })
        return res.data
    } catch (error: any) {
        console.error('[joinRoom] 加入房间失败', {
            code,
            nickname,
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            message: error.message,
        })
        throw error
    }
}

export async function startRoom(code: string, hostToken: string): Promise<QuestionPush> {
    console.log('[startRoom] 开始调用API', { code, hostToken: hostToken ? '已提供' : '缺失' })
    try {
        const res = await api.post(
            `/rooms/${code}/start`,
            null,
            { headers: { 'X-Host-Token': hostToken } }
        )
        console.log('[startRoom] API调用成功', { data: res.data })
        return res.data
    } catch (error: any) {
        console.error('[startRoom] API调用失败', {
            code,
            status: error.response?.status,
            statusText: error.response?.statusText,
            data: error.response?.data,
            message: error.message,
        })
        throw error
    }
}

export async function getCurrentQuestion(code: string): Promise<QuestionPush | null> {
    const res = await api.get(`/rooms/${code}/current`, { validateStatus: () => true })
    if (res.status === 204) return null
    if (res.status >= 200 && res.status < 300) return res.data
    throw new Error(`Failed to get current question: ${res.status}`)
}

export async function getRandomQuestions(count: number): Promise<QuestionResponse[]> {
    const res = await api.get(`/questions/random?count=${count}`)
    return res.data
}

export async function getQuestionById(id: number): Promise<QuestionResponse> {
    const res = await api.get(`/questions/${id}`)
    return res.data
}

export async function getRoomQuestions(code: string): Promise<QuestionWithAnswer[]> {
    const res = await api.get(`/rooms/${code}/questions`)
    return res.data
}

export async function nextQuestion(code: string, hostToken: string): Promise<QuestionPush> {
    const res = await api.post(
        `/rooms/${code}/next`,
        null,
        { headers: { 'X-Host-Token': hostToken } }
    )
    return res.data
}
