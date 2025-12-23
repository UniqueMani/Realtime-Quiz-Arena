import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Host from './views/Host.vue'
import Player from './views/Player.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/host/:code', component: Host },
    { path: '/player/:code/:playerId', component: Player }
  ]
})
