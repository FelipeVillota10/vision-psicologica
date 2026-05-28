<template>
  <div class="dashboard-container">
    <header class="main-header">
      <div class="header-content">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="header-logo" />
        <h1>Visión Psicológica TM</h1>
      </div>
    </header>

    <div class="workspace">
      <aside class="sidebar">
        <ul class="menu-list">
          <li class="menu-item-simple" @click="vistaActual = 'bienvenida'">
            <span class="menu-icon">📅</span>
            Citas
          </li>

          <li class="menu-item" @click="toggleMenu('historias')">
            <div class="menu-title">
              <span class="menu-icon">📖</span>
              Historias 
              <span class="arrow">{{ menus.historias ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.historias" class="submenu" @click.stop>
              <li>Crear Historia</li>
              <li>Buscar Historia</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('clientes')">
            <div class="menu-title">
              <span class="menu-icon">👤</span>
              Clientes 
              <span class="arrow">{{ menus.clientes ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.clientes" class="submenu" @click.stop>
              <li @click="vistaActual = 'crearCliente'">Crear Cliente</li>
              <li>Buscar Cliente</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('ordenamiento')">
            <div class="menu-title">
              <span class="menu-icon">📋</span>
              Ordenamiento 
              <span class="arrow">{{ menus.ordenamiento ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.ordenamiento" class="submenu" @click.stop>
              <li>Remisiones</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('afiliados')">
            <div class="menu-title">
              <span class="menu-icon">🤝</span>
              Afiliados 
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

        <div class="welcome-box" v-else>
          <h2>Bienvenido/a <span class="user-name"></span></h2>
          <p>Por favor, elija una opción del menú de la izquierda.</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// MODIFICACIÓN 3: Importamos la vista que creaste y declaramos la variable de estado
import RegistrarClienteView from './RegistrarClienteView.vue' 

const vistaActual = ref('bienvenida') // Por defecto arranca mostrando la bienvenida

const menus = ref({
  historias: false,
  clientes: false,
  ordenamiento: false,
  afiliados: false,
})

const toggleMenu = (menuName: 'historias' | 'clientes' | 'ordenamiento' | 'afiliados') => {
  menus.value[menuName] = !menus.value[menuName]
}
</script>

<style scoped>
/* ====================== HEADER ====================== */
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-header {
  background-color: #d3d4f5;
  color: rgb(0, 0, 0);
  padding: 0.8rem 1.5rem;
  display: flex;
  align-items: center;
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

/* ====================== SIDEBAR ====================== */
.workspace {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 280px;
  background-color: #d3d4f5;
  color: #000000;
  padding-top: 0;
  box-shadow: 1px 0 2px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  border-top: 1px solid #1e1e1e;
  border-right: 1px solid #1e1e1e;
  border-left: 1px solid #1e1e1e;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* Estilos para iconos */
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
  transition: background 0.2s;
  display: flex;
  align-items: center;
}

.menu-title {
  justify-content: space-between;
}

.arrow {
  margin-left: auto;
  font-size: 1rem;
  opacity: 0.8;
}

.menu-item-simple:hover,
.menu-title:hover {
  background-color: #b8b9e0;
}

.submenu {
  list-style: none;
  padding: 0;
  background-color: #c5c6eb;
}

.submenu li {
  padding: 0.85rem 2.8rem;
  cursor: pointer;
  color: #2c2c2c;
  transition: all 0.2s;
}

.submenu li:hover {
  background-color: #a8a9d8;
  color: black;
  padding-left: 3rem;
}

/* ====================== MAIN CONTENT ====================== */
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
  box-shadow: 0 6px 20px rgba(48, 48, 48, 0.08);
  max-width: 520px;
  width: 100%;
}

.welcome-box h2 {
  color: #1e3a5f;
  font-size: 2.2rem;
  margin: 0 0 1rem 0;
}

.user-name {
  color: #2c8a6e;
  font-weight: 500;
}

.welcome-box p {
  color: #5f6b7a;
  font-size: 1.15rem;
}

/* mondongo 🗣*/
</style>