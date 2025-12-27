<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSessionStore } from '../stores/session'
import { getRoomQuestions, type QuestionWithAnswer } from '../lib/api'

const route = useRoute()
const router = useRouter()
const s = useSessionStore()

const code = computed(() => String(route.params.code || s.roomCode || '').toUpperCase())
const userAnswers = computed(() => {
  const answers = s.userAnswers || {}
  return new Map(Object.entries(answers).map(([k, v]) => [Number(k), v as string]))
})

const loading = ref(true)
const err = ref('')
const questions = ref<QuestionWithAnswer[]>([])
const expandedExplanations = ref<Set<number>>(new Set())

const stats = computed(() => {
  if (questions.value.length === 0) {
    return { total: 0, correct: 0, accuracy: 0 }
  }
  
  let correct = 0
  questions.value.forEach(q => {
    const userAnswer = userAnswers.value.get(q.id)
    if (userAnswer === q.correctAnswer) {
      correct++
    }
  })
  
  const total = questions.value.length
  const accuracy = total > 0 ? Math.round((correct / total) * 100) : 0
  
  return { total, correct, accuracy }
})

function toggleExplanation(id: number) {
  if (expandedExplanations.value.has(id)) {
    expandedExplanations.value.delete(id)
  } else {
    expandedExplanations.value.add(id)
  }
}

function isCorrect(question: QuestionWithAnswer): boolean {
  const userAnswer = userAnswers.value.get(question.id)
  return userAnswer === question.correctAnswer
}

function getUserAnswer(question: QuestionWithAnswer): string | undefined {
  return userAnswers.value.get(question.id)
}

onMounted(async () => {
  if (!code.value) {
    router.push('/')
    return
  }

  loading.value = true
  try {
    const qs = await getRoomQuestions(code.value)
    questions.value = qs
  } catch (e: any) {
    err.value = e?.response?.data?.message ?? '获取题目失败'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-emerald-100 via-teal-100 to-cyan-100"></div>
    <div class="relative z-10 mx-auto max-w-5xl px-4 py-8">
      <!-- Header -->
      <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6 mb-6">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-extrabold text-slate-800">答题结果</h1>
            <p class="mt-2 text-sm text-slate-600">房间码：<span class="font-semibold">{{ code }}</span></p>
          </div>
          <button
              @click="router.push('/')"
              class="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:opacity-95"
          >
            返回首页
          </button>
        </div>
      </div>

      <!-- Statistics -->
      <div v-if="!loading && questions.length > 0" class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6 mb-6">
        <h2 class="text-xl font-extrabold text-slate-800 mb-4">📊 统计信息</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="rounded-2xl bg-gradient-to-br from-blue-50 to-blue-100 p-4 border border-blue-200">
            <div class="text-sm text-blue-600 font-semibold">正确题数</div>
            <div class="text-3xl font-extrabold text-blue-800 mt-1">
              {{ stats.correct }} / {{ stats.total }}
            </div>
          </div>
          <div class="rounded-2xl bg-gradient-to-br from-emerald-50 to-emerald-100 p-4 border border-emerald-200">
            <div class="text-sm text-emerald-600 font-semibold">正确率</div>
            <div class="text-3xl font-extrabold text-emerald-800 mt-1">
              {{ stats.accuracy }}%
            </div>
          </div>
          <div class="rounded-2xl bg-gradient-to-br from-purple-50 to-purple-100 p-4 border border-purple-200">
            <div class="text-sm text-purple-600 font-semibold">总题数</div>
            <div class="text-3xl font-extrabold text-purple-800 mt-1">
              {{ stats.total }}
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-12 text-center">
        <div class="text-4xl mb-4">⏳</div>
        <div class="text-lg font-semibold text-slate-800">加载中...</div>
      </div>

      <!-- Error -->
      <div v-if="err" class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-pink-200 p-6 mb-6">
        <div class="text-pink-700">{{ err }}</div>
      </div>

      <!-- Questions List -->
      <div v-if="!loading && questions.length > 0" class="space-y-4">
        <div
            v-for="(q, index) in questions"
            :key="q.id"
            class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6"
        >
          <div class="flex items-start justify-between mb-4">
            <div class="flex-1">
              <div class="flex items-center gap-3 mb-2">
                <span class="rounded-full bg-slate-900 text-white w-8 h-8 flex items-center justify-center text-sm font-bold">
                  {{ index + 1 }}
                </span>
                <span v-if="q.category" class="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                  {{ q.category }}
                </span>
                <span
                    v-if="isCorrect(q)"
                    class="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700"
                >
                  ✅ 正确
                </span>
                <span
                    v-else
                    class="rounded-full bg-pink-100 px-3 py-1 text-xs font-semibold text-pink-700"
                >
                  ❌ 错误
                </span>
              </div>
              <h3 class="text-lg font-extrabold text-slate-800">{{ q.stem }}</h3>
            </div>
          </div>

          <div class="grid gap-3 sm:grid-cols-2 mb-4">
            <div
                v-for="opt in q.options"
                :key="opt"
                class="rounded-2xl border px-4 py-3"
                :class="{
                  'border-emerald-300 bg-emerald-50': opt === q.correctAnswer,
                  'border-pink-300 bg-pink-50': getUserAnswer(q) === opt && opt !== q.correctAnswer,
                  'border-slate-200 bg-white/70': opt !== q.correctAnswer && getUserAnswer(q) !== opt
                }"
            >
              <div class="flex items-center justify-between">
                <div class="font-semibold" :class="{
                  'text-emerald-800': opt === q.correctAnswer,
                  'text-pink-800': getUserAnswer(q) === opt && opt !== q.correctAnswer,
                  'text-slate-700': opt !== q.correctAnswer && getUserAnswer(q) !== opt
                }">
                  {{ opt }}
                </div>
                <div>
                  <span v-if="opt === q.correctAnswer" class="text-emerald-600 font-bold">✓ 正确答案</span>
                  <span v-else-if="getUserAnswer(q) === opt" class="text-pink-600 font-bold">✗ 你的答案</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="getUserAnswer(q)" class="mb-4">
            <div class="text-sm text-slate-600">
              <span class="font-semibold">你的答案：</span>
              <span :class="isCorrect(q) ? 'text-emerald-700 font-bold' : 'text-pink-700 font-bold'">
                {{ getUserAnswer(q) }}
              </span>
            </div>
            <div class="text-sm text-slate-600 mt-1">
              <span class="font-semibold">正确答案：</span>
              <span class="text-emerald-700 font-bold">{{ q.correctAnswer }}</span>
            </div>
          </div>
          <div v-else class="mb-4 text-sm text-slate-500">
            未作答
          </div>

          <div v-if="q.explanation" class="border-t border-slate-200 pt-4">
            <button
                @click="toggleExplanation(q.id)"
                class="flex items-center justify-between w-full text-left"
            >
              <span class="font-semibold text-slate-800">题目解释</span>
              <span class="text-slate-600">{{ expandedExplanations.has(q.id) ? '▼' : '▶' }}</span>
            </button>
            <div
                v-if="expandedExplanations.has(q.id)"
                class="mt-3 rounded-2xl bg-slate-50 border border-slate-200 p-4 text-sm text-slate-700"
            >
              {{ q.explanation }}
            </div>
          </div>
        </div>
      </div>

      <!-- No Questions -->
      <div v-if="!loading && questions.length === 0" class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-12 text-center">
        <div class="text-4xl mb-4">📝</div>
        <div class="text-lg font-semibold text-slate-800">暂无题目</div>
      </div>
    </div>
  </div>
</template>

