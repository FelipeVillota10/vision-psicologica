import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import RecoverView from '../views/RecoverView.vue'
import HistoriaView from '../views/HistoriaView.vue'
import BuscarClienteView from '../views/BuscarClienteView.vue'
import BuscarHistoriaView from '../views/BuscarHistoriaView.vue'
import ModificarHistoriaView from '../views/ModificarHistoriaView.vue'
import CrearCitaView from '../views/CrearCitaView.vue'
import ConfiguracionUsuarioView from '../views/ConfiguracionUsuarioView.vue'
import CrearConsultaView from '../views/CrearConsultaView.vue'
import ModificarConsultaView from '../views/ModificarConsultaView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/registro',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/recuperacion',
      name: 'recover',
      component: RecoverView,
    },
    {
      path: '/historia',
      name: 'historia',
      component: HistoriaView,
    },
    {
      path: '/buscarCliente',
      name: 'buscarCliente',
      component: BuscarClienteView,
    },
    {
      path: '/buscar-historia',
      name: 'buscar-historia',
      component: BuscarHistoriaView,
    },
    {
      path: '/modificar-historia',
      name: 'modificar-historia',
      component: ModificarHistoriaView,
    },
    {
      path: '/crear-cita',
      name: 'crear-cita',
      component: CrearCitaView,
    },
    {
      path: '/configuracion-usuario',
      name: 'configuracion-usuario',
      component: ConfiguracionUsuarioView,
    },
    {
      path: '/crear-consulta',
      name: 'crear-consulta',
      component: CrearConsultaView,
    },
    {
      path: '/modificar-consulta',
      name: 'modificar-consulta',
      component: ModificarConsultaView,
    },
  ],
})

export default router
