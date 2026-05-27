<template>
  <div class="dashboard-container">
    <header class="main-header">
      <h1>Visión Psicológica</h1>
    </header>

    <div class="workspace">
      <aside class="sidebar">
        <ul class="menu-list">
          <li class="menu-item-simple">Citas</li>

          <li class="menu-item" @click="toggleMenu('historias')">
            <div class="menu-title">
              Historias <span>{{ menus.historias ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.historias" class="submenu" @click.stop>
              <li>Crear</li>
              <li>Buscar</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('clientes')">
            <div class="menu-title">
              Clientes <span>{{ menus.clientes ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.clientes" class="submenu" @click.stop>
              <li>Crear</li>
              <li>Buscar</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('ordenamiento')">
            <div class="menu-title">
              Ordenamiento <span>{{ menus.ordenamiento ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.ordenamiento" class="submenu" @click.stop>
              <li>Remisiones</li>
            </ul>
          </li>

          <li class="menu-item" @click="toggleMenu('afiliados')">
            <div class="menu-title">
              Afiliados <span>{{ menus.afiliados ? '▼' : '►' }}</span>
            </div>
            <ul v-if="menus.afiliados" class="submenu" @click.stop>
              <li>Crear</li>
              <li>Buscar</li>
            </ul>
          </li>
        </ul>
      </aside>

      <main class="main-content">
        <div class="welcome-box">
          <h2>Bienvenido</h2>
          <p>Por favor, elija una opción del menú de la izquierda.</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// Definimos un objeto reactivo con TypeScript para rastrear qué menús están abiertos (true) o cerrados (false)
const menus = ref({
  historias: false,
  clientes: false,
  ordenamiento: false,
  afiliados: false,
})

// Función para alternar el estado del menú seleccionado
const toggleMenu = (menuName: 'historias' | 'clientes' | 'ordenamiento' | 'afiliados') => {
  menus.value[menuName] = !menus.value[menuName]
}
</script>

<style scoped>
/* Estructura general de la pantalla */
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* Estilo del Bloque Superior */
.main-header {
  background-color: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.main-header h1 {
  margin: 0;
  font-size: 1.8rem;
}

/* Contenedor del área de trabajo */
.workspace {
  display: flex;
  flex: 1; /* Ocupa todo el espacio restante abajo del header */
}

/* Estilo del Bloque Izquierdo (Sidebar) */
.sidebar {
  width: 260px;
  background-color: #34495e;
  color: #ecf0f1;
  padding-top: 1rem;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.05);
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item-simple {
  padding: 1rem 1.5rem;
  font-weight: bold;
  cursor: pointer;
  border-bottom: 1px solid #2c3e50;
  transition: background 0.2s;
}

.menu-item-simple:hover {
  background-color: #2c3e50;
}

.menu-item {
  border-bottom: 1px solid #2c3e50;
}

.menu-title {
  padding: 1rem 1.5rem;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  user-select: none;
}

.menu-title:hover {
  background-color: #2c3e50;
}

.menu-title span {
  font-size: 0.8rem;
  color: #bdc3c7;
}

/* Submenús desplegables */
.submenu {
  list-style: none;
  padding: 0;
  background-color: #2c3e50;
}

.submenu li {
  padding: 0.75rem 2.5rem;
  cursor: pointer;
  font-size: 0.95rem;
  color: #bdc3c7;
  transition:
    color 0.2s,
    background 0.2s;
}

.submenu li:hover {
  background-color: #1a252f;
  color: white;
}

/* Estilo del Bloque Derecho (Main Content) */
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f4f6f9;
  padding: 2rem;
}

.welcome-box {
  text-align: center;
  background: white;
  padding: 3rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  max-width: 500px;
  width: 100%;
}

.welcome-box h2 {
  color: #2c3e50;
  margin-top: 0;
  font-size: 2rem;
}

.welcome-box p {
  color: #7f8c8d;
  font-size: 1.1rem;
  margin-bottom: 0;
}
</style>
