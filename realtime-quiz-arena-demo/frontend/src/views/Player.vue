<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Client } from '@stomp/stompjs'
import { useSessionStore } from '../stores/session'
import { connectStomp } from '../lib/ws'
import type { LeaderboardEntry, LeaderboardPush, QuestionPush } from '../lib/api'

const route = useRoute()
const router = useRouter()
const s = useSessionStore()

const code = computed(() => String(route.params.code || s.roomCode || '').toUpperCase())
const playerId = computed(() => String(route.params.playerId || s.playerId || ''))
const nickname = computed(() => s.nickname || 'Player')

const err = ref('')
const submitted = ref(false)
const selected = ref<string>('')

const leaderboard = ref<LeaderboardEntry[]>([])

let client: Client | null = null

// Demo：固定题（后端也支持 questionId = -1 且正确答案 Mars）
const question = ref<QuestionPush>({
  questionId: -1,
  stem: 'Demo question: Which planet is known as the Red Planet?',
  options: ['Earth', 'Mars', 'Jupiter', 'Venus'],
  openedAtEpochMs: 0,
  closedAtEpochMs: 0,
})

function parseLeaderboard(payload: any) {
  if (Array.isArray(payload)) return { entries: payload, serverTimeEpochMs: Date.now() }
  return payload as LeaderboardPush
}

function pick(opt: string) {
  selected.value = opt
  submitted.value = false
  err.value = ''
}

function submit() {
  err.value = ''
  if (!selected.value) {
    err.value = '先选一个选项再提交呀～'
    return
  }
  if (!client || !client.connected) {
    err.value = 'WebSocket 还没连上，请稍等 1 秒～'
    return
  }

  client.publish({
    destination: `/app/room/${code.value}/answer`,
    body: JSON.stringify({
      playerId: playerId.value,
      answer: selected.value,
      questionId: question.value.questionId,
      clientTimestampMs: Date.now(),
    }),
  })

  submitted.value = true
}

onMounted(() => {
  if (!code.value || !playerId.value) router.push('/')

  client = connectStomp((c) => {
    c.subscribe(`/topic/room/${code.value}/leaderboard`, (msg) => {
      try {
        const data = parseLeaderboard(JSON.parse(msg.body))
        leaderboard.value = (data.entries || []).slice().sort((a: any, b: any) => (b.totalScore ?? 0) - (a.totalScore ?? 0))
      } catch {}
    })
  })
})

onBeforeUnmount(() => {
  if (client) client.deactivate()
})
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-sky-100 via-purple-100 to-pink-100"></div>
    <div class="relative z-10 mx-auto max-w-5xl px-4 py-8">
      <div class="grid gap-6 lg:grid-cols-[1fr_360px]">
        <!-- Left: question -->
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div class="flex items-center gap-3">
              <span class="text-2xl">🧸</span>
              <div>
                <div class="text-sm text-slate-500">Player</div>
                <div class="text-xl font-extrabold text-slate-800">{{ nickname }}</div>
              </div>
            </div>
            <div class="rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white">
              Room: <span class="tracking-widest">{{ code }}</span>
            </div>
          </div>

          <div class="mt-6 rounded-3xl bg-white/70 border border-slate-100 p-5">
            <div class="text-lg font-extrabold text-slate-800"> {{ question.stem }}</div>
            <p class="mt-2 text-sm text-slate-600">
              选一个答案提交后，主持人页面的排行榜会实时变化～
            </p>

            <div class="mt-5 grid gap-3 sm:grid-cols-2">
              <button
                  v-for="opt in question.options"
                  :key="opt"
                  @click="pick(opt)"
                  class="group rounded-3xl border px-4 py-4 text-left shadow-sm transition active:scale-[0.99]"
                  :class="selected === opt
                  ? 'border-pink-300 bg-pink-50'
                  : 'border-slate-200 bg-white/70 hover:bg-slate-50'"
              >
                <div class="flex items-center justify-between">
                  <div class="font-semibold text-slate-800">{{ opt }}</div>
                  <span class="text-xl" v-if="selected === opt">✅</span>
                  <span class="text-xl opacity-40 group-hover:opacity-80" v-else></span>
                </div>
              </button>
            </div>

            <div class="mt-5 flex flex-wrap gap-3">
              <button
                  @click="submit"
                  class="rounded-2xl bg-gradient-to-r from-sky-500 to-indigo-500 px-5 py-3 font-semibold text-white shadow hover:opacity-95"
              >
                提交答案
              </button>

              <div v-if="submitted" class="rounded-2xl bg-emerald-50 border border-emerald-200 px-4 py-3 text-emerald-700">
                提交成功！看右侧排行榜是否上升～
              </div>
            </div>

            <div v-if="err" class="mt-4 rounded-2xl border border-pink-200 bg-white/70 px-4 py-3 text-pink-700">
              {{ err }}
            </div>
          </div>
        </div>

        <!-- Right: leaderboard -->
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-extrabold text-slate-800">⚡ 实时排行榜</h2>
            <span class="text-xs text-slate-500">自动刷新</span>
          </div>

          <div v-if="leaderboard.length === 0" class="mt-4 text-sm text-slate-500">
            还没有分数～先提交一个答案试试！
          </div>

          <ul v-else class="mt-4 space-y-2">
            <li
                v-for="(p, idx) in leaderboard"
                :key="p.playerId"
                class="flex items-center justify-between rounded-2xl bg-white/70 px-3 py-2 border border-slate-100"
            >
              <div class="flex items-center gap-2">
                <span class="w-8 text-center font-bold">
                  {{ idx === 0 ? '🥇' : idx === 1 ? '🥈' : idx === 2 ? '🥉' : `#${idx + 1}` }}
                </span>
                <span class="font-semibold text-slate-800">{{ p.nickname }}</span>
              </div>
              <span class="rounded-full bg-indigo-100 px-3 py-1 text-sm font-bold text-indigo-700">
                {{ p.totalScore }}
              </span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
