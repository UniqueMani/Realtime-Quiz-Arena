<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { submitSpeedAnswer, type QuestionResponse } from '../lib/api'

const route = useRoute()
const router = useRouter()
const sessionId = route.params.sessionId as string

// 状态变量
const question = ref<QuestionResponse | null>(null)
const index = ref(1) // 当前第几题
const total = ref(10)
const timeLeft = ref(15) // 倒计时
const score = ref(0) // 当前得分

// UI 交互变量
const selectedOption = ref<string>('')
const isSubmitting = ref(false)
const showResultAnim = ref(false) // 是否显示 +100 / Miss 动画
const lastResult = ref({ correct: false, addScore: 0 })

let timerInterval: any = null

// 1. 初始化：从 router state 获取第一题
onMounted(() => {
  const state = history.state as any
  if (state && state.firstQuestion) {
    question.value = JSON.parse(state.firstQuestion)
    total.value = state.total || 10
    startTimer()
  } else {
    // 如果没有state（比如刷新了页面），为了安全起见回首页
    // 实际项目中可以增加一个 fetchCurrentStatus API
    alert("会话中断，请重新开始")
    router.push('/')
  }
})

// 2. 计时器逻辑
function startTimer() {
  clearInterval(timerInterval)
  timeLeft.value = 15 // 重置时间
  timerInterval = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      submitAnswer(null) // 时间到，自动提交空答案
    }
  }, 1000)
}

// 3. 用户点击选项
function onSelect(opt: string) {
  if (isSubmitting.value) return
  selectedOption.value = opt
  submitAnswer(opt)
}

// 4. 提交答案核心逻辑
async function submitAnswer(ans: string | null) {
  if (isSubmitting.value) return
  isSubmitting.value = true
  clearInterval(timerInterval) // 停止计时

  try {
    if (!question.value) return
    const res = await submitSpeedAnswer(sessionId, question.value.id, ans)

    // 更新总分
    score.value = res.totalScore

    // 展示结果动画
    lastResult.value = { correct: res.correct, addScore: res.scoreEarned }
    showResultAnim.value = true

    // 延迟 1.5 秒后切换
    setTimeout(() => {
      showResultAnim.value = false

      if (res.finished) {
        router.push(`/speed/${sessionId}/result`)
      } else {
        question.value = res.nextQuestion
        index.value++
        selectedOption.value = ''
        isSubmitting.value = false
        startTimer()
      }
    }, 1500)

  } catch (e) {
    alert('提交失败，请检查网络')
    router.push('/')
  }
}

onBeforeUnmount(() => clearInterval(timerInterval))
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background (same style as Host) -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-purple-100 via-pink-100 to-sky-100"></div>

    <div class="relative z-10 mx-auto max-w-3xl px-4 py-10">
      <!-- Top info -->
      <div class="grid gap-4 md:grid-cols-3">
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 md:col-span-2">
          <div class="flex items-center justify-between gap-4">
            <div class="flex items-center gap-3">
              <span class="text-2xl">⚡</span>
              <div>
                <div class="text-sm text-slate-500">Speed Challenge</div>
                <div class="text-2xl font-extrabold text-slate-800">第 {{ index }} / {{ total }} 题</div>
              </div>
            </div>

            <div class="text-right">
              <div class="text-sm text-slate-500">当前得分</div>
              <div class="text-3xl font-extrabold text-slate-900">{{ score }}</div>
            </div>
          </div>

          <p class="mt-2 text-xs text-slate-500">
            每题 15 秒，点击选项立即提交。
          </p>
        </div>

        <!-- Timer -->
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-5 flex items-center justify-center">
          <div class="relative w-24 h-24">
            <div class="absolute inset-0 rounded-full bg-white/60"></div>
            <div class="absolute inset-0 flex items-center justify-center">
              <div class="text-center">
                <div class="text-3xl font-extrabold" :class="timeLeft <= 5 ? 'text-rose-600' : 'text-slate-900'">
                  {{ timeLeft }}
                </div>
                <div class="text-[10px] font-semibold text-slate-500 tracking-wider">SECONDS</div>
              </div>
            </div>

            <svg class="absolute inset-0 w-full h-full -rotate-90 pointer-events-none" viewBox="0 0 100 100">
              <circle cx="50" cy="50" r="46" fill="none" stroke="#e2e8f0" stroke-width="6" />
              <circle
                  cx="50"
                  cy="50"
                  r="46"
                  fill="none"
                  :stroke="timeLeft <= 5 ? '#ef4444' : '#8b5cf6'"
                  stroke-width="6"
                  stroke-dasharray="289"
                  :stroke-dashoffset="289 - (289 * timeLeft) / 15"
                  stroke-linecap="round"
                  class="transition-all duration-1000 ease-linear"
              />
            </svg>
          </div>
        </div>
      </div>

      <!-- Question card -->
      <div
          v-if="question"
          class="mt-6 rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6 md:p-8 relative overflow-hidden"
      >
        <transition name="fade">
          <div
              v-if="showResultAnim"
              class="absolute inset-0 z-20 flex flex-col items-center justify-center bg-white/80 backdrop-blur"
          >
            <div class="text-6xl mb-3">
              {{ lastResult.correct ? '🎉' : '🫠' }}
            </div>
            <div
                class="text-4xl font-extrabold tracking-tight"
                :class="lastResult.correct ? 'text-emerald-600' : 'text-slate-400'"
            >
              {{ lastResult.correct ? '+' + lastResult.addScore : 'Miss' }}
            </div>
          </div>
        </transition>

        <div class="mb-5">
          <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider">题目</div>
          <h2 class="mt-2 text-xl md:text-2xl font-bold text-slate-800 leading-snug">
            {{ question.stem }}
          </h2>
        </div>

        <div class="grid gap-3">
          <button
              v-for="opt in question.options"
              :key="opt"
              @click="onSelect(opt)"
              :disabled="isSubmitting"
              class="group w-full rounded-2xl border px-4 py-3 text-left font-semibold transition shadow-sm disabled:opacity-60 disabled:cursor-not-allowed"
              :class="
              selectedOption === opt
                ? 'border-violet-300 bg-violet-50 text-violet-800'
                : 'border-white/60 bg-white/60 text-slate-700 hover:bg-white/80 hover:border-slate-200'
            "
          >
            <span
                class="inline-flex items-center justify-center w-8 h-8 rounded-full text-sm font-extrabold mr-3 transition-colors"
                :class="
                selectedOption === opt
                  ? 'bg-violet-600 text-white'
                  : 'bg-slate-100 text-slate-500 group-hover:bg-violet-100 group-hover:text-violet-600'
              "
            >
              ?
            </span>
            {{ opt }}
          </button>
        </div>

        <div class="mt-5 flex items-center justify-between text-xs text-slate-500">
          <div>提示：选中选项后会自动提交</div>
          <div v-if="isSubmitting" class="font-semibold">提交中…</div>
        </div>
      </div>

      <!-- Fallback when question not ready -->
      <div
          v-else
          class="mt-6 rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-12 text-center"
      >
        <div class="text-4xl mb-4">⏳</div>
        <div class="text-lg font-semibold text-slate-700">加载题目中…</div>
      </div>
    </div>
  </div>
</template>


<style scoped>
/* 简单的淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>