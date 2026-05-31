<template>
  <div class="dashboard-container">
    <header class="main-header">
      <div class="header-content">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="header-logo" />
        <h1>Visión Psicológica TM</h1>
      </div>

      <div class="header-profile" @click="toggleProfileMenu">
        <span class="profile-icon">👤</span>
        <ul v-if="showProfileMenu" class="profile-menu" @click.stop>
          <li class="logout-btn" @click="cerrarSesion">Cerrar Sesión</li>
        </ul>
      </div>
    </header>

    <div class="workspace">
      <aside class="sidebar">
        <ul class="menu-list">
          <li class="menu-item" @click="toggleMenu('citas')">
            <div class="menu-title">
              <span class="menu-icon">📅</span> Citas
              <span class="arrow">{{ menus.citas ? '▼' : '►' }}</span>
            </div>

            <ul v-if="menus.citas" class="submenu" @click.stop>
              <li @click="vistaActual = 'crearCita'">Crear Cita</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('historias')">
            <div class="menu-title">
              <span class="menu-icon">📖</span> Historias
              <span class="arrow">{{ menus.historias ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.historias" class="submenu" @click.stop>
              <li @click="vistaActual = 'crearHistoria'">Crear Historia</li>
              <li @click="vistaActual = 'buscarHistoria'">Buscar Historia</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('clientes')">
            <div class="menu-title">
              <span class="menu-icon">👤</span> Clientes
              <span class="arrow">{{ menus.clientes ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.clientes" class="submenu" @click.stop>
              <li @click="vistaActual = 'crearCliente'">Crear Cliente</li>
              <li @click="vistaActual = 'buscarCliente'">Buscar Cliente</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('ordenamiento')">
            <div class="menu-title">
              <span class="menu-icon">📋</span> Ordenamiento
              <span class="arrow">{{ menus.ordenamiento ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.ordenamiento" class="submenu" @click.stop>
              <li>Remisiones</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('afiliados')">
            <div class="menu-title">
              <span class="menu-icon">🤝</span> Afiliados
              <span class="arrow">{{ menus.afiliados ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.afiliados" class="submenu" @click.stop>
              <li>Crear Afiliado</li>
              <li>Buscar Afiliado</li>
            </ul>
          </li>
        </ul>
      </aside>

      <main class="main-content">
        <RegistrarClienteView v-if="vistaActual === 'crearCliente'" />
        <BuscarClienteView v-else-if="vistaActual === 'buscarCliente'" />
        <HistoriaView v-else-if="vistaActual === 'crearHistoria'" />
        <BuscarHistoriaView v-else-if="vistaActual === 'buscarHistoria'" />
        <CrearCitaView v-else-if="vistaActual === 'crearCita'" />

        <div class="welcome-box" v-else>
          <h2>Bienvenido/a</h2>
          <p>Por favor, elija una opción del menú de la izquierda.</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import RegistrarClienteView from './RegistrarClienteView.vue'
import BuscarClienteView from './BuscarClienteView.vue'
import HistoriaView from './HistoriaView.vue'
import BuscarHistoriaView from './BuscarHistoriaView.vue'
import CrearCitaView from './CrearCitaView.vue'

const router = useRouter()

const vistaActual = ref('bienvenida')
const showProfileMenu = ref(false)

const menus = ref({
  citas: false,
  historias: false,
  clientes: false,
  ordenamiento: false,
  afiliados: false,
})

const toggleMenu = (
  menuName: 'citas' | 'historias' | 'clientes' | 'ordenamiento' | 'afiliados',
) => {
  menus.value[menuName] = !menus.value[menuName]
}

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value
}

const cerrarSesion = async () => {
  if (!confirm('¿Desea cerrar sesión?')) {
    showProfileMenu.value = false
    return
  }

  try {
    const response = await fetch('http://localhost:8080/usuario/logout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    })

    if (response.ok) {
      router.push('/')
    } else {
      alert('Error al cerrar sesión')
    }
  } catch (error) {
    console.error('Error de red:', error)
    alert('No se pudo conectar con el servidor')
  }
}
</script>

<style scoped>
/* Estilos Header */
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.main-header {
  background-color: #d3d4f5;
  padding: 0.8rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #1e1e1e;
}
.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.header-logo {
  width: 65px;
  height: 65px;
  object-fit: contain;
}
.main-header h1 {
  margin: 0;
  font-size: 1.9rem;
  font-weight: 600;
}

/* Estilos Perfil */
.header-profile {
  position: relative;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
}
.profile-menu {
  position: absolute;
  top: 110%;
  right: 0;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  list-style: none;
  padding: 0;
  margin: 0;
  min-width: 160px;
  z-index: 1000;
}
.profile-menu li {
  padding: 0.8rem 1rem;
  cursor: pointer;
  color: #333;
}
.logout-btn {
  color: #d9534f !important;
  font-weight: bold;
}
.profile-menu li:hover {
  background-color: #f0f0f0;
}

/* Mantener tus estilos anteriores de .workspace, .sidebar, .main-content aquí abajo... */
.workspace {
  display: flex;
  flex: 1;
}
.sidebar {
  width: 280px;
  background-color: #d3d4f5;
  color: #000000;
  overflow-y: auto;
  border: 1px solid #1e1e1e;
}
.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.menu-icon {
  display: inline-block;
  width: 24px;
  margin-right: 12px;
  font-size: 1.3rem;
  text-align: center;
}
.menu-item-simple,
.menu-title {
  padding: 1rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 1px solid #344a73;
  display: flex;
  align-items: center;
}
.menu-title {
  justify-content: space-between;
}
.arrow {
  margin-left: auto;
  font-size: 1rem;
}
.submenu {
  list-style: none;
  padding: 0;
  background-color: #c5c6eb;
}
.submenu li {
  padding: 0.85rem 2.8rem;
  cursor: pointer;
}
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ecf5f5;
  padding: 2rem;
}
.welcome-box {
  text-align: center;
  background: #e0f5f5;
  padding: 4rem 3rem;
  border-radius: 12px;
}
</style>
