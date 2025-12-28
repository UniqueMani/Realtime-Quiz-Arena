<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useSessionStore } from '../stores/session'
import { createRoom, joinRoom, startSpeedGame } from '../lib/api'

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

// 1. 创建房间逻辑 (Host)
async function onCreateRoom() {
  err.value = ''
  const nick = nickname.value.trim()
  if (!nick) {
    err.value = '先取一个独一无二的昵称吧～'
    return
  }

  creating.value = true
  try {
    const { roomCode, hostToken } = await createRoom()
    s.roomCode = roomCode
    s.hostToken = hostToken
    s.nickname = nick
    s.playerId = 'HOST' // 主持人标识

    router.push(`/host/${roomCode}`)
  } catch (e: any) {
    err.value = '创建失败: ' + (e.response?.data?.message || '未知错误')
  } finally {
    creating.value = false
  }
}

// 2. 加入房间逻辑 (Player)
async function onJoinRoom() {
  err.value = ''
  const code = normCode(joinCode.value)
  const nick = nickname.value.trim()

  if (!code || !nick) {
    err.value = '房间号和昵称都不能少哦'
    return
  }

  joining.value = true
  try {
    const { playerId, token } = await joinRoom(code, nick)
    s.roomCode = code
    s.nickname = nick
    s.playerId = playerId
    s.playerToken = token

    router.push(`/player/${code}/${playerId}`)
  } catch (e: any) {
    err.value = '加入失败: ' + (e.response?.data?.message || '房间不存在或已满')
  } finally {
    joining.value = false
  }
}

// 3. 快问快答逻辑 (Speed Mode - New!)
async function onStartSpeed() {
  err.value = ''
  const nick = nickname.value.trim()
  if (!nick) {
    err.value = '玩快问快答也要有个名字呀～'
    return
  }

  // 这里为了简单复用 loading 状态，也可以单独设一个 speedLoading
  joining.value = true
  try {
    // 调用后端接口
    const res = await startSpeedGame(nick)

    s.nickname = nick

    // 跳转到答题页，并通过 router state 传递第一题数据
    // 这样进入页面时不需要再次 fetch
    router.push({
      name: 'SpeedGame',
      params: { sessionId: res.sessionId },
      state: {
        firstQuestion: JSON.stringify(res.firstQuestion),
        total: res.totalQuestions
      }
    })
  } catch (e: any) {
    err.value = '启动失败：' + (e.response?.data?.message || '未知错误')
  } finally {
    joining.value = false
  }
}
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background (same style as Host) -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-purple-100 via-pink-100 to-sky-100"></div>

    <div class="relative z-10 mx-auto max-w-5xl px-4 py-10">
      <!-- Title -->
      <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6 mb-8 text-center">
        <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight text-slate-900">
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-violet-600 to-pink-500">Quiz Arena</span>
        </h1>
        <p class="mt-2 text-sm md:text-base text-slate-600">
          实时竞技 · 知识对决 · 极速挑战
        </p>

        <div
            v-if="err"
            class="mt-4 mx-auto max-w-lg rounded-2xl bg-rose-50/80 px-4 py-3 text-sm font-semibold text-rose-700 border border-rose-200"
        >
          {{ err }}
        </div>
      </div>

      <!-- Cards -->
      <div class="grid gap-6 md:grid-cols-3 items-stretch">
        <!-- Host -->
        <section class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 flex flex-col">
          <div class="flex items-center gap-3">
            <div>
              <div class="text-sm text-slate-500">作为主持人</div>
              <div class="text-lg font-bold text-slate-800">新建比赛</div>
            </div>
          </div>

          <div class="mt-4 space-y-3 flex-1">
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">你的昵称</label>
              <input
                  v-model="nickname"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-pink-500/20 focus:border-pink-300 transition"
                  placeholder="主持人昵称"
              />
            </div>

            <button
                @click="onCreateRoom"
                :disabled="creating"
                class="w-full rounded-2xl bg-gradient-to-r from-pink-500 to-violet-500 px-4 py-3 text-sm font-semibold text-white shadow hover:opacity-95 active:scale-[0.99] transition disabled:opacity-60"
            >
              {{ creating ? '创建中…' : '新建比赛 →' }}
            </button>

            <p class="text-xs text-slate-500">
              创建后进入 Host 控制台，你可以发题、查看排行榜。
            </p>
          </div>
        </section>

        <!-- Player -->
        <section class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 flex flex-col">
          <div class="flex items-center gap-3">
            <div>
              <div class="text-sm text-slate-500">作为选手</div>
              <div class="text-lg font-bold text-slate-800">加入比赛</div>
            </div>
          </div>

          <div class="mt-4 space-y-3 flex-1">
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">房间码</label>
              <input
                  v-model="joinCode"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-300 transition tracking-widest uppercase"
                  placeholder="如 ABCD"
              />
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">你的昵称</label>
              <input
                  v-model="nickname"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-sky-500/20 focus:border-sky-300 transition"
                  placeholder="选手名称"
              />
            </div>

            <button
                @click="onJoinRoom"
                :disabled="joining"
                class="w-full rounded-2xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white shadow hover:opacity-95 active:scale-[0.99] transition disabled:opacity-60"
            >
              {{ joining ? '加入中…' : '立即加入' }}
            </button>

            <p class="text-xs text-slate-500">
              输入主持人提供的房间码加入，发题后即可作答。
            </p>
          </div>
        </section>

        <!-- Speed -->
        <section class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 flex flex-col">
          <div class="flex items-center gap-3">
            <div class="flex-1">
              <div class="flex items-center justify-between gap-3">
                <div>
                  <div class="text-sm text-slate-500">Solo</div>
                  <div class="text-lg font-bold text-slate-800">快问快答</div>
                </div>
                <span class="rounded-full bg-white/70 border border-white/60 px-3 py-1 text-xs font-semibold text-slate-700">
                  15 秒/题
                </span>
              </div>
            </div>
          </div>

          <div class="mt-4 space-y-3 flex-1">
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">你的昵称</label>
              <input
                  v-model="nickname"
                  class="w-full rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-300 transition"
                  placeholder="用于计分与展示"
              />
            </div>

            <button
                @click="onStartSpeed"
                :disabled="joining"
                class="w-full rounded-2xl bg-gradient-to-r from-amber-400 to-orange-500 px-4 py-3 text-sm font-semibold text-white shadow hover:opacity-95 active:scale-[0.99] transition disabled:opacity-60"
            >
              {{ joining ? '启动中…' : '开始挑战' }}
            </button>

            <p class="text-xs text-slate-500">
              快速作答累积分数，挑战结束后可查看题目解析。
            </p>
          </div>
        </section>
      </div>

      <div class="mt-10 text-center text-xs text-slate-500">
        &copy; 2025 Java Enterprise Application Project
      </div>
    </div>
  </div>
</template>
