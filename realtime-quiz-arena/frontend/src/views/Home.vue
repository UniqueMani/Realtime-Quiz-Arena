<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '../stores/session'
import { createRoom, joinRoom } from '../lib/api'

const router = useRouter()
const s = useSessionStore()

const joinCode = ref('')
const nickname = ref('')

const creating = ref(false)
const joining = ref(false)
const err = ref('')

function normCode(v: string) {
  return v.trim().toUpperCase()
}

async function onCreateRoom() {
  err.value = ''
  const nick = nickname.value.trim()
  if (!nick) {
    err.value = '先取一个独一无二的昵称吧～ '
    return
  }

  creating.value = true
  try {
    const { roomCode, hostToken } = await createRoom()
    s.roomCode = roomCode
    s.hostToken = hostToken
    s.nickname = nick
    s.playerId = ''
    router.push(`/host/${roomCode}`)
  } catch (e: any) {
    err.value = e?.response?.data?.message ?? '创建房间失败了，稍后再试试～'
  } finally {
    creating.value = false
  }
}

async function onJoinRoom() {
  err.value = ''
  const code = normCode(joinCode.value)
  const nick = nickname.value.trim()

  if (!code) {
    err.value = '请输入房间码（比如 ABCD）'
    return
  }
  if (!nick) {
    err.value = '昵称不能为空噢～'
    return
  }

  joining.value = true
  try {
    const { playerId, nickname: serverNick } = await joinRoom(code, nick)
    s.roomCode = code
    s.playerId = playerId
    s.nickname = serverNick ?? nick
    s.hostToken = ''
    router.push(`/player/${code}/${playerId}`)
  } catch (e: any) {
    err.value = e?.response?.data?.message ?? '加入失败：房间码不对或房间未创建～'
  } finally {
    joining.value = false
  }
}
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background (works even if #app has max-width) -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-pink-100 via-purple-100 to-sky-100"></div>
    <!-- cute blobs -->
    <div class="pointer-events-none fixed -top-24 -left-24 h-72 w-72 rounded-full bg-pink-300/40 blur-3xl animate-pulse"></div>
    <div class="pointer-events-none fixed top-20 -right-24 h-80 w-80 rounded-full bg-sky-300/40 blur-3xl animate-pulse"></div>
    <div class="pointer-events-none fixed -bottom-24 left-1/3 h-72 w-72 rounded-full bg-purple-300/40 blur-3xl animate-pulse"></div>

    <div class="relative z-10 mx-auto max-w-4xl px-4 py-10">
      <header class="text-center">
        <div class="inline-flex items-center gap-2 rounded-full bg-white/60 px-4 py-2 shadow-sm backdrop-blur">
          <span class="font-semibold text-slate-700">Realtime Quiz Arena</span>
        </div>

        <h1 class="mt-5 text-4xl font-extrabold tracking-tight text-slate-800">
          来一局 <span class="text-pink-500">实时答题擂台</span> 吧！
        </h1>
        <p class="mt-3 text-slate-600">
          创建房间 → 分享房间码 → 大家一起抢答，看排行榜实时变化
        </p>
      </header>

      <div class="mt-10 grid gap-6 md:grid-cols-2">
        <!-- Create -->
        <section class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-bold text-slate-800">我是主持人</h2>
            <span class="rounded-full bg-pink-100 px-3 py-1 text-sm text-pink-700">Host</span>
          </div>

          <label class="mt-4 block text-sm font-medium text-slate-700">你的昵称</label>
          <input
              v-model="nickname"
              class="mt-2 w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 outline-none focus:ring-4 focus:ring-pink-200"
              placeholder="比如：小熊软糖"
          />

          <button
              @click="onCreateRoom"
              :disabled="creating"
              class="mt-5 w-full rounded-2xl bg-gradient-to-r from-pink-500 to-purple-500 px-4 py-3 font-semibold text-white shadow-lg hover:opacity-95 active:scale-[0.99] disabled:opacity-60"
          >
            {{ creating ? '创建中…' : '创建房间' }}
          </button>

          <p class="mt-3 text-sm text-slate-600">
            创建后会进入主持人页面，可开始出题并查看排行榜。
          </p>
        </section>

        <!-- Join -->
        <section class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-bold text-slate-800">我是选手</h2>
            <span class="rounded-full bg-sky-100 px-3 py-1 text-sm text-sky-700">Player</span>
          </div>

          <label class="mt-4 block text-sm font-medium text-slate-700">房间码</label>
          <input
              v-model="joinCode"
              class="mt-2 w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 uppercase tracking-widest outline-none focus:ring-4 focus:ring-sky-200"
              placeholder="ABCD"
          />

          <label class="mt-4 block text-sm font-medium text-slate-700">你的昵称</label>
          <input
              v-model="nickname"
              class="mt-2 w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 outline-none focus:ring-4 focus:ring-sky-200"
              placeholder="比如：闪电猫猫"
          />

          <button
              @click="onJoinRoom"
              :disabled="joining"
              class="mt-5 w-full rounded-2xl bg-gradient-to-r from-sky-500 to-indigo-500 px-4 py-3 font-semibold text-white shadow-lg hover:opacity-95 active:scale-[0.99] disabled:opacity-60"
          >
            {{ joining ? '加入中…' : ' 加入房间' }}
          </button>

          <p class="mt-3 text-sm text-slate-600">
            加入后进入答题页，提交答案会触发排行榜实时刷新。
          </p>
        </section>
      </div>

      <div v-if="err" class="mt-6 rounded-2xl border border-pink-200 bg-white/70 px-4 py-3 text-pink-700 shadow-sm">
        {{ err }}
      </div>
    </div>
  </div>
</template>
