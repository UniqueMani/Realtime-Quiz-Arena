import { defineStore } from 'pinia'

export const useSessionStore = defineStore('session', {
  state: () => ({
    roomCode: '' as string,
    hostToken: '' as string,
    playerId: '' as string,
    nickname: '' as string,
    userAnswers: {} as Record<number, string>
  }),
  getters: {
    getUserAnswersMap(): Map<number, string> {
      return new Map(Object.entries(this.userAnswers).map(([k, v]) => [Number(k), v]))
    }
  }
})
