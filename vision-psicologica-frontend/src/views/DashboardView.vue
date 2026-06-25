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
          <li @click="irAPerfil">Vea su perfil</li>
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
              <li @click="cambiarVista('crearCita')">Crear Cita</li>
              <li @click="cambiarVista('buscarCita')">Buscar Citas</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('historias')">
            <div class="menu-title">
              <span class="menu-icon">📖</span> Historias
              <span class="arrow">{{ menus.historias ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.historias" class="submenu" @click.stop>
              <li @click="cambiarVista('crearHistoria')">Crear Historia</li>
              <li @click="cambiarVista('buscarHistoria')">Buscar Historia</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('clientes')">
            <div class="menu-title">
              <span class="menu-icon">👤</span> Clientes
              <span class="arrow">{{ menus.clientes ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.clientes" class="submenu" @click.stop>
              <li @click="cambiarVista('crearCliente')">Crear Cliente</li>
              <li @click="cambiarVista('buscarCliente')">Buscar Cliente</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('psicologos')">
            <div class="menu-title">
              <span class="menu-icon">🩺</span> Psicólogos
              <span class="arrow">{{ menus.psicologos ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.psicologos" class="submenu" @click.stop>
              <li @click="cambiarVista('psicologos')">Ver Psicólogos</li>
            </ul>
          </li>

          

          <li class="menu-item" @click="toggleMenu('consultas')">
            <div class="menu-title">
              <span class="menu-icon">🧑‍⚕️</span> Consultas
              <span class="arrow">{{ menus.consultas ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.consultas" class="submenu" @click.stop>
              <li @click="cambiarVista('crearConsulta')">Crear Consulta</li>
              <li @click="cambiarVista('modificarConsulta')">Modificar Consulta</li>
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
        <BuscarCitaView v-else-if="vistaActual === 'buscarCita'" />
        <PsicologosView v-else-if="vistaActual === 'psicologos'" />
        
        <CrearConsultaView v-else-if="vistaActual === 'crearConsulta'" />
        <ModificarConsultaView v-else-if="vistaActual === 'modificarConsulta'" />
        
        <ConfiguracionUsuario v-else-if="vistaActual === 'configuracionUsuario'" />

        <div class="welcome-box" v-else>
          <h2>Panel de Control</h2>
          <p class="welcome-text">Bienvenido/a al sistema. Aquí tienes el resumen operacional de tus pacientes asignados en tiempo real.</p>
          
          <div class="metrics-grid">
            <div class="metric-card">
              <div class="card-icon">👥</div>
              <div class="card-data">
                <span class="metric-label">Total Pacientes Registrados</span>
                <h3 class="metric-value" v-if="totalPacientes > 0">{{ totalPacientes }} pacientes</h3>
                <h3 class="metric-value empty" v-else>0 pacientes</h3>
              </div>
              <p class="empty-suggestion" v-if="totalPacientes === 0">Aún no tienes pacientes registrados en tu cuenta.</p>
              
              <button class="btn-goto-list" @click="vistaActual = 'buscarCliente'">
                Ver lista completa de pacientes →
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RegistrarClienteView from './RegistrarClienteView.vue'
import BuscarClienteView from './BuscarClienteView.vue'
import HistoriaView from './HistoriaView.vue'
import BuscarHistoriaView from './BuscarHistoriaView.vue'
import CrearCitaView from './CrearCitaView.vue'
import BuscarCitaView from './BuscarCitaView.vue'
import PsicologosView from './PsicologosView.vue'
// IMPORTACIÓN DEL NUEVO COMPONENTE
import ConfiguracionUsuario from './ConfiguracionUsuarioView.vue'
import CrearConsultaView from './CrearConsultaView.vue'
import ModificarConsultaView from './ModificarConsultaView.vue'

const router = useRouter()

const vistaActual = ref('bienvenida')
const showProfileMenu = ref(false)
const totalPacientes = ref(0)

const menus = ref({
  citas: false,
  historias: false,
  clientes: false,
  psicologos: false,
  consultas: false,
})

const consultarTotalPacientes = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/clientes')
    if (response.ok) {
      const clientes = await response.json()
      totalPacientes.value = clientes.length
    }
  } catch (error) {
    console.error('Error al cargar métricas del dashboard:', error)
  }
}

const cambiarVista = (vista: string) => {
  vistaActual.value = vista
  if (vista === 'bienvenida') {
    consultarTotalPacientes()
  }
}

const toggleMenu = (
  menuName: 'citas' | 'historias' | 'clientes' | 'psicologos' | 'consultas',
) => {
  menus.value[menuName] = !menus.value[menuName]
}

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value
}

// NUEVA FUNCIÓN PARA REDIRIGIR AL PERFIL
const irAPerfil = () => {
  cambiarVista('configuracionUsuario')
  showProfileMenu.value = false // Oculta el menú desplegable tras hacer click
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

onMounted(() => {
  consultarTotalPacientes()
})
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

/* Estilos de estructura */
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

/* Estilos de Bienvenida y Widget de Métrica Responsive (HU-29) */
.welcome-box {
  width: 100%;
  max-width: 750px;
  background: white;
  padding: 3rem 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  text-align: left;
}
.welcome-box h2 {
  color: #344a73;
  margin-top: 0;
  font-size: 2rem;
}
.welcome-text {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 2rem;
  line-height: 1.5;
}

/* Diseño de la Tarjeta de Métricas */
.metrics-grid {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
.metric-card {
  background: linear-gradient(135deg, #f4f5f9 0%, #e8eaf6 100%);
  border-left: 5px solid #7e80da;
  border-radius: 12px;
  padding: 1.75rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  position: relative;
  transition: transform 0.2s ease;
}
.metric-card:hover {
  transform: translateY(-2px);
}
.card-icon {
  font-size: 2.2rem;
  background: white;
  width: 55px;
  height: 55px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.card-data {
  display: flex;
  flex-direction: column;
}
.metric-label {
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #718096;
  font-weight: 600;
}
.metric-value {
  font-size: 2.2rem;
  margin: 0.25rem 0 0 0;
  color: #1a202c;
  font-weight: 700;
}
.metric-value.empty {
  color: #a0aec0;
}
.empty-suggestion {
  font-size: 0.95rem;
  color: #e53e3e;
  margin: 0;
  font-weight: 500;
}
.btn-goto-list {
  background: none;
  border: none;
  padding: 0;
  color: #7e80da;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  text-align: left;
  align-self: flex-start;
  transition: color 0.2s ease;
}
.btn-goto-list:hover {
  color: #344a73;
  text-decoration: underline;
}

/* Reglas Media Query para Responsive */
@media (max-width: 768px) {
  .welcome-box {
    padding: 2rem 1rem;
  }
  .metric-value {
    font-size: 1.8rem;
  }
}
</style>