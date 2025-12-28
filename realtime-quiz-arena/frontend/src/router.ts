import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Host from './views/Host.vue'
import Player from './views/Player.vue'
import Result from './views/Result.vue'
import SpeedGame from './views/SpeedGame.vue'
import SpeedResult from './views/SpeedResult.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/host/:code', component: Host },
    { path: '/player/:code/:playerId', component: Player },
    { path: '/room/:code/result', component: Result },
      // ============ 新增路由 ============
      {
          path: '/speed/:sessionId',
          name: 'SpeedGame',
          component: SpeedGame
      },
      {
          path: '/speed/:sessionId/result',
          name: 'SpeedResult',
          component: SpeedResult
      }
      // =================================
  ]
})
