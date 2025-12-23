import { defineStore } from 'pinia'

export const useSessionStore = defineStore('session', {
  state: () => ({
    roomCode: '' as string,
    hostToken: '' as string,
    playerId: '' as string,
    nickname: '' as string
  })
})
