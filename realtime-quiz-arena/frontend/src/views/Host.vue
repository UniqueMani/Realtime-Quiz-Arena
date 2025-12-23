<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Client } from '@stomp/stompjs'
import { useSessionStore } from '../stores/session'
import { connectStomp } from '../lib/ws'
import { startRoom, type LeaderboardEntry, type LeaderboardPush, type QuestionPush } from '../lib/api'

const route = useRoute()
const router = useRouter()
const s = useSessionStore()

const code = computed(() => String(route.params.code || s.roomCode || '').toUpperCase())
const hostToken = computed(() => s.hostToken)

const starting = ref(false)
const err = ref('')

const question = ref<QuestionPush | null>(null)
const leaderboard = ref<LeaderboardEntry[]>([])
const serverTime = ref<number>(Date.now())
const now = ref<number>(Date.now())

let client: Client | null = null
let timer: any = null

function parseLeaderboard(payload: any) {
  // 兼容两种：{entries, serverTimeEpochMs} 或直接 entries[]
  if (Array.isArray(payload)) return { entries: payload, serverTimeEpochMs: Date.now() }
  return payload as LeaderboardPush
}

async function onStart() {
  err.value = ''
  if (!hostToken.value) {
    err.value = '缺少 hostToken：请从首页“创建房间”进入主持人页～'
    return
  }

  starting.value = true
  try {
    const q = await startRoom(code.value, hostToken.value)
    question.value = q
  } catch (e: any) {
    err.value = e?.response?.data?.message ?? '开始失败：请检查后端是否正常运行'
  } finally {
    starting.value = false
  }
}

async function copyRoomCode() {
  try {
    await navigator.clipboard.writeText(code.value)
  } catch {}
}

function badge(rank: number) {
  if (rank === 1) return '🥇'
  if (rank === 2) return '🥈'
  if (rank === 3) return '🥉'
  return `#${rank}`
}

const progress = computed(() => {
  if (!question.value) return 0
  const start = question.value.openedAtEpochMs || 0
  const end = question.value.closedAtEpochMs || 0
  if (!start || !end || end <= start) return 0
  const p = (now.value - start) / (end - start)
  return Math.max(0, Math.min(1, p))
})

const timeLeft = computed(() => {
  if (!question.value?.closedAtEpochMs) return ''
  const ms = question.value.closedAtEpochMs - now.value
  if (ms <= 0) return '已结束'
  const s = Math.ceil(ms / 1000)
  return `${s}s`
})

onMounted(() => {
  if (!code.value) router.push('/')

  client = connectStomp((c) => {
    c.subscribe(`/topic/room/${code.value}/leaderboard`, (msg) => {
      try {
        const data = parseLeaderboard(JSON.parse(msg.body))
        leaderboard.value = (data.entries || []).slice().sort((a: any, b: any) => (b.totalScore ?? 0) - (a.totalScore ?? 0))
        serverTime.value = data.serverTimeEpochMs ?? Date.now()
      } catch {}
    })
  })

  timer = setInterval(() => {
    now.value = Date.now()
  }, 200)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
  if (client) client.deactivate()
})
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-purple-100 via-pink-100 to-sky-100"></div>
    <div class="relative z-10 mx-auto max-w-5xl px-4 py-8">
      <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5">
          <div class="flex items-center gap-3">
            <span class="text-2xl">🎤</span>
            <div>
              <div class="text-sm text-slate-500">Host Room</div>
              <div class="text-2xl font-extrabold tracking-widest text-slate-800">{{ code }}</div>
            </div>
          </div>
          <div class="mt-3 flex flex-wrap gap-2">
            <button
                @click="copyRoomCode"
                class="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:opacity-95"
            >
              复制房间码
            </button>
            <button
                @click="onStart"
                :disabled="starting"
                class="rounded-2xl bg-gradient-to-r from-pink-500 to-purple-500 px-4 py-2 text-sm font-semibold text-white shadow hover:opacity-95 disabled:opacity-60"
            >
              {{ starting ? '启动中…' : ' 开始（发出题目）' }}
            </button>
          </div>
          <p class="mt-2 text-xs text-slate-500">
            选手在首页输入房间码即可加入；提交答案后排行榜会实时刷新。
          </p>
        </div>

        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 md:w-[360px]">
          <div class="flex items-center justify-between">
            <div class="font-bold text-slate-800">排行榜</div>
            <div class="text-xs text-slate-500">实时更新 ⚡</div>
          </div>

          <div v-if="leaderboard.length === 0" class="mt-4 text-sm text-slate-500">
            还没有人得分～让同学们快来答题！
          </div>

          <ul v-else class="mt-4 space-y-2">
            <li
                v-for="(p, idx) in leaderboard"
                :key="p.playerId"
                class="flex items-center justify-between rounded-2xl bg-white/70 px-3 py-2 border border-slate-100"
            >
              <div class="flex items-center gap-2">
                <span class="w-10 text-center font-bold">{{ badge(idx + 1) }}</span>
                <span class="font-semibold text-slate-800">{{ p.nickname }}</span>
              </div>
              <span class="rounded-full bg-pink-100 px-3 py-1 text-sm font-bold text-pink-700">
                {{ p.totalScore }}
              </span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Question -->
      <div class="mt-6 rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-extrabold text-slate-800">当前题目</h2>
          <div v-if="question" class="text-sm text-slate-600">
            ⏳ 剩余：<span class="font-bold text-slate-800">{{ timeLeft }}</span>
          </div>
        </div>

        <div v-if="!question" class="mt-4 text-slate-500">
          还没开始～点击上面的「开始」按钮发出第一题。
        </div>

        <div v-else class="mt-4">
          <div class="rounded-2xl bg-white/70 border border-slate-100 p-4">
            <div class="text-slate-800 font-semibold">{{ question.stem }}</div>

            <div class="mt-4 grid gap-3 sm:grid-cols-2">
              <div
                  v-for="opt in question.options"
                  :key="opt"
                  class="rounded-2xl border border-slate-200 bg-white/70 px-4 py-3 text-slate-700"
              >
                {{ opt }}
              </div>
            </div>

            <div class="mt-4 h-2 w-full rounded-full bg-slate-200 overflow-hidden">
              <div class="h-full rounded-full bg-gradient-to-r from-pink-500 to-purple-500" :style="{ width: `${progress * 100}%` }"></div>
            </div>

            <p class="mt-3 text-xs text-slate-500">
              （题目由后端 /rooms/{code}/start 返回；选手页面默认展示同题即可提交答案。）
            </p>
          </div>
        </div>

        <div v-if="err" class="mt-4 rounded-2xl border border-pink-200 bg-white/70 px-4 py-3 text-pink-700">
          {{ err }}
        </div>
      </div>
    </div>
  </div>
</template>
