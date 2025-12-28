<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getSpeedResult, type SpeedGameResultResponse } from '../lib/api'

const route = useRoute()
const router = useRouter()
const sessionId = route.params.sessionId as string
const result = ref<SpeedGameResultResponse | null>(null)

// 控制题目解析的展开/收起
const expanded = ref<Set<number>>(new Set())

function toggle(id: number) {
  if (expanded.value.has(id)) expanded.value.delete(id)
  else expanded.value.add(id)
}

onMounted(async () => {
  try {
    result.value = await getSpeedResult(sessionId)
  } catch (e) {
    alert("无法获取战绩，请重试")
  }
})
</script>

<template>
  <div class="min-h-screen w-full relative overflow-hidden">
    <!-- full-viewport gradient background (same style as Result) -->
    <div class="pointer-events-none fixed inset-0 -z-10 bg-gradient-to-br from-emerald-100 via-teal-100 to-cyan-100"></div>

    <div class="relative z-10 mx-auto max-w-5xl px-4 py-8">
      <!-- Loading -->
      <div
          v-if="!result"
          class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-12 text-center"
      >
        <div class="text-4xl mb-4">⏳</div>
        <div class="text-lg font-semibold text-slate-700">正在加载战绩…</div>
      </div>

      <div v-else>
        <!-- Header -->
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6 mb-6">
          <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div>
              <h1 class="text-3xl font-extrabold text-slate-800">快问快答结果</h1>
              <p class="mt-2 text-sm text-slate-600">
                选手：<span class="font-semibold">{{ result.nickname }}</span>
              </p>
            </div>

            <button
                @click="router.push('/')"
                class="rounded-2xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:opacity-95"
            >
              返回首页
            </button>
          </div>
        </div>

        <!-- Stats -->
        <div class="grid gap-4 md:grid-cols-4 mb-6">
          <div class="rounded-2xl bg-gradient-to-br from-emerald-50 to-emerald-100 p-4 border border-emerald-200">
            <div class="text-sm text-emerald-600 font-semibold">总分</div>
            <div class="text-3xl font-extrabold text-emerald-800 mt-1">{{ result.totalScore }}</div>
          </div>

          <div class="rounded-2xl bg-gradient-to-br from-sky-50 to-sky-100 p-4 border border-sky-200">
            <div class="text-sm text-sky-600 font-semibold">正确题数</div>
            <div class="text-3xl font-extrabold text-sky-800 mt-1">{{ result.correctCount }}</div>
          </div>

          <div class="rounded-2xl bg-gradient-to-br from-purple-50 to-purple-100 p-4 border border-purple-200">
            <div class="text-sm text-purple-600 font-semibold">总题数</div>
            <div class="text-3xl font-extrabold text-purple-800 mt-1">{{ result.details.length }}</div>
          </div>

          <div class="rounded-2xl bg-gradient-to-br from-amber-50 to-amber-100 p-4 border border-amber-200">
            <div class="text-sm text-amber-700 font-semibold">正确率</div>
            <div class="text-3xl font-extrabold text-amber-900 mt-1">
              {{ result.details.length ? Math.round((result.correctCount / result.details.length) * 100) : 0 }}%
            </div>
          </div>
        </div>

        <!-- Questions -->
        <div class="rounded-3xl bg-white/70 backdrop-blur shadow-xl border border-white/60 p-6">
          <div class="flex flex-col gap-1 md:flex-row md:items-center md:justify-between">
            <div class="text-lg font-bold text-slate-800">题目回顾</div>
            <div class="text-sm text-slate-500">点击“查看解析”展开</div>
          </div>

          <ul class="mt-4 space-y-3">
            <li
                v-for="(q, idx) in result.details"
                :key="q.id"
                class="rounded-2xl bg-white/70 px-4 py-4 border border-white/60"
            >
              <div class="flex items-start gap-3">
                <div class="mt-0.5 w-8 h-8 rounded-full bg-slate-900 text-white text-sm font-extrabold flex items-center justify-center">
                  {{ idx + 1 }}
                </div>

                <div class="flex-1">
                  <div class="font-semibold text-slate-800 leading-snug">
                    {{ q.stem }}
                  </div>

                  <div class="mt-3 flex flex-wrap items-center gap-2 text-sm">
                    <div class="px-3 py-1 rounded-lg bg-emerald-50 text-emerald-700 font-semibold border border-emerald-100">
                      正确答案：{{ q.correctAnswer }}
                    </div>
                  </div>

                  <div class="mt-4 pt-3 border-t border-slate-100">
                    <button
                        @click="toggle(q.id)"
                        class="inline-flex items-center gap-2 text-sm font-semibold text-slate-700 hover:text-slate-900"
                    >
                      {{ expanded.has(q.id) ? '收起解析' : '查看解析' }}
                      <span class="text-xs transition-transform" :class="expanded.has(q.id) ? 'rotate-180' : ''">▼</span>
                    </button>

                    <div
                        v-if="expanded.has(q.id)"
                        class="mt-3 rounded-2xl bg-slate-50 border border-slate-100 p-4 text-sm text-slate-700 leading-relaxed animate-fade-in"
                    >
                      {{ q.explanation || '暂无解析' }}
                    </div>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>