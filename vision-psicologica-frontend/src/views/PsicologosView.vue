<template>
  <div class="psicologos-container">
    <div class="psicologos-card">

      <div class="psicologos-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="psicologos-logo" />
        <div class="title-container">
          <h2>Psicólogos del sistema</h2>
          <p class="subtitle">Consulta el personal registrado en la plataforma</p>
        </div>
      </div>

      <div class="filtros-grid">
        <div class="filtro-field">
          <label class="field-label">Buscar por nombre o ID</label>
          <div class="search-bar">
            <span class="search-icon">🔍</span>
            <input
              v-model="filtroNombre"
              type="text"
              placeholder="Digite el nombre o identificación..."
              class="search-input"
            />
            <button v-if="filtroNombre" class="clear-btn" @click="filtroNombre = ''">✕</button>
          </div>
        </div>
      </div>
      <div class="stats-header">

  <span class="conteo-total">
    Total: <strong>{{ psicologosFiltrados.length }} psicólogo{{ psicologosFiltrados.length !== 1 ? 's' : '' }}</strong>
  </span>

</div>
      <div class="tabla-wrapper">
        <table class="tabla-datos">
          <thead>
            <tr>
              <th>#</th>
              <th>Nombre completo</th>
              <th>Identificación</th>
              <th>Email</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(p, index) in psicologosPaginados" :key="p.id">
              <td class="td-index">{{ (paginaActual - 1) * porPagina + index + 1 }}</td>
              <td class="td-nombre">{{ p.nombre }}</td>
              <td class="txt-bold">{{ p.identificacion }}</td>
              <td>{{ p.correoElectronico }}</td>
              <td>
                <div class="actions-group">
                  <button class="btn-accion btn-ver" @click="verDetalle(p)">👁 Ver</button>
                  <button class="btn-accion btn-editar" @click="abrirEdicion(p)">✏️ Editar</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="totalPaginas > 1" class="paginacion">
        <button class="btn-pag" :disabled="paginaActual === 1" @click="paginaActual--">‹ Anterior</button>
        <span class="pag-info">Pág {{ paginaActual }} de {{ totalPaginas }}</span>
        <button class="btn-pag" :disabled="paginaActual === totalPaginas" @click="paginaActual++">Siguiente ›</button>
      </div>
    </div>

    <div v-if="psicologoSeleccionado" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <div class="modal-header-row">
          <h3>{{ modalTipo === 'ver' ? 'Detalle' : 'Editar' }} Psicólogo</h3>
        </div>
        
        <div class="modal-grid">
          <div class="modal-field full-width">
            <label class="field-label">Nombre</label>
            <input v-if="modalTipo === 'editar'" v-model="psicologoEditado.nombre" class="field-input" />
            <span v-else class="field-value">{{ psicologoSeleccionado.nombre }}</span>
          </div>
          <div class="modal-field">
            <label class="field-label">Identificación</label>
            <input v-if="modalTipo === 'editar'" v-model="psicologoEditado.identificacion" class="field-input" />
            <span v-else class="field-value">{{ psicologoSeleccionado.identificacion }}</span>
          </div>
          <div class="modal-field">
            <label class="field-label">Email</label>
            <input v-if="modalTipo === 'editar'" v-model="psicologoEditado.correoElectronico" class="field-input" />
            <span v-else class="field-value">{{ psicologoSeleccionado.correoElectronico }}</span>
          </div>
        </div>

        <div class="button-group">
          <button v-if="modalTipo === 'editar'" class="btn-primary" @click="guardarCambios">Guardar</button>
          <button class="btn-secondary" @click="cerrarModal">Cerrar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

interface Psicologo {
  id: number
  nombre: string
  identificacion: string
  correoElectronico: string 
}

const psicologos = ref<Psicologo[]>([])
const filtroNombre = ref('')
const paginaActual = ref(1)
const porPagina = 8
const modalTipo = ref<'ver' | 'editar' | null>(null)
const psicologoSeleccionado = ref<Psicologo | null>(null)
const psicologoEditado = ref<Partial<Psicologo>>({})

const API_URL = 'http://localhost:8080/usuario' 

const obtenerPsicologos = async () => {
  try {
    const response = await fetch(API_URL) 
    if (response.ok) {
      const data = await response.json()
      
      // Mapeamos explícitamente para asegurar que el dato llegue
      psicologos.value = data.map((u: any) => ({
        id: u.id,
        nombre: u.nombre,
        identificacion: u.identificacion,
        // Aquí le decimos: "Usa correoElectronico, si no existe, busca email"
        correoElectronico: u.correoElectronico || u.email || 'Sin correo'
      }))
    }
  } catch (error) {
    console.error("Error al cargar:", error)
  }
}

const psicologosFiltrados = computed(() => {
  return psicologos.value.filter(p => 
    p.nombre?.toLowerCase().includes(filtroNombre.value.toLowerCase()) || 
    p.identificacion?.includes(filtroNombre.value)
  )
})

const totalPaginas = computed(() => Math.max(1, Math.ceil(psicologosFiltrados.value.length / porPagina)))
const psicologosPaginados = computed(() => {
  const inicio = (paginaActual.value - 1) * porPagina
  return psicologosFiltrados.value.slice(inicio, inicio + porPagina)
})

const verDetalle = (p: Psicologo) => { psicologoSeleccionado.value = p; modalTipo.value = 'ver' }
const abrirEdicion = (p: Psicologo) => { psicologoSeleccionado.value = p; psicologoEditado.value = { ...p }; modalTipo.value = 'editar' }
const cerrarModal = () => { psicologoSeleccionado.value = null; modalTipo.value = null }

const guardarCambios = async () => {
  try {
    await fetch(`${API_URL}/${psicologoEditado.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(psicologoEditado.value)
    })
    alert('Actualizado con éxito')
    cerrarModal()
    obtenerPsicologos()
  } catch{ alert('Error al guardar') }
}

onMounted(obtenerPsicologos)
</script>

<style scoped>
/* ── Contenedor principal ── */
.psicologos-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

/* ── Card principal ── */
.psicologos-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 1150px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

/* ── Encabezado ── */
.psicologos-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.psicologos-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  flex-shrink: 0;
}

.title-container { flex: 1; }

h2 {
  color: #0f6e56;
  margin: 0 0 0.4rem 0;
  font-size: 1.8rem;
}

.subtitle {
  color: #5f6b7a;
  margin: 0;
  font-size: 1rem;
}

/* ── Filtros ── */
.filtros-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1rem 1.5rem;
  margin-bottom: 1.2rem;
}

.filtro-field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.field-label {
  font-size: 0.82rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: #0f6e56;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: #f8fdfb;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  padding: 0.6rem 1rem;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.search-bar:focus-within {
  border-color: #1d9e75;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

.search-icon { font-size: 1.1rem; flex-shrink: 0; }

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 1rem;
  color: #1a202c;
  outline: none;
}

.clear-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  color: #5f6b7a;
  padding: 0;
  transition: color 0.2s;
}
.clear-btn:hover { color: #e74c3c; }

.field-input {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  font-size: 1rem;
  background: #f8fdfb;
  color: #1a202c;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
  font-family: inherit;
}

.field-input:focus {
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

/* ── Barra de acciones ── */
.barra-acciones {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.2rem;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.conteo-total { font-size: 0.98rem; color: #5f6b7a; }
.conteo-total strong { color: #0f6e56; }

.export-group { display: flex; gap: 0.6rem; }

.btn-export {
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-export:hover { opacity: 0.88; transform: translateY(-1px); }

.btn-excel { background: #d3f5ef; color: #0f6e56; }
.btn-pdf   { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }

/* ── Estado vacío ── */
.estado-info {
  text-align: center;
  padding: 3rem 1rem;
  color: #5f6b7a;
  font-size: 1rem;
}
.empty-icon { font-size: 2.5rem; display: block; margin-bottom: 0.5rem; }

/* Contador más grande y llamativo */
.stats-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1.2rem;
  padding-right: 0.5rem;
}
.conteo-total {
  font-size: 1.1rem; /* Aumentamos el tamaño de fuente */
  color: #0f6e56;
  background: #e6f7f2; /* Un fondo un poquito más sólido */
  padding: 0.6rem 1.2rem; /* Más espacio interno (padding) */
  border-radius: 12px;
  border: 1px solid #9fe1cb;
  font-weight: 500;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05); /* Un toque de sombra para que resalte */
}

.conteo-total strong {
  font-size: 1.25rem; /* El número lo hacemos aún más grande */
  color: #0b5743;
  margin-left: 5px;
}

/* ── Tabla ── */
.tabla-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #c8f0e6;
  margin-bottom: 1.2rem;
}

.tabla-datos {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.93rem;
  background: white;
}

.tabla-datos thead tr {
  background: linear-gradient(135deg, #baf5f2, #9fe1cb);
  color: #0f6e56;
  text-align: left;
}

.tabla-datos th {
  padding: 0.85rem 1rem;
  font-weight: 700;
  white-space: nowrap;
}

.tabla-datos tbody tr {
  border-bottom: 1px solid #e6f7f2;
  transition: background 0.15s;
}
.tabla-datos tbody tr:hover { background: #f8fdfb; }

.tabla-datos td {
  padding: 0.8rem 1rem;
  color: #2d3748;
  vertical-align: middle;
}

.td-index  { color: #9fe1cb; font-weight: 700; width: 40px; }
.td-nombre { font-weight: 600; color: #0f6e56; }
.txt-bold  { font-weight: 700; }

/* ── Badges ── */
.badge-estado {
  display: inline-block;
  padding: 0.2rem 0.85rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
  white-space: nowrap;
}
.badge-activo   { background: #c6f6d5; color: #276749; }
.badge-inactivo { background: #fed7d7; color: #9b2c2c; }

/* ── Botones de acción en tabla ── */
.actions-group {
  display: flex;
  gap: 0.4rem;
  flex-wrap: nowrap;
}

.btn-accion {
  border: none;
  border-radius: 8px;
  padding: 0.38rem 0.75rem;
  font-size: 0.82rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-accion:hover { opacity: 0.85; transform: translateY(-1px); }

.btn-ver        { background: #d3f5ef; color: #0f6e56; }
.btn-editar     { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-desactivar { background: #ffd6d6; color: #c53030; }
.btn-activar    { background: #c6f6d5; color: #276749; }

/* ── Paginación ── */
.paginacion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.2rem;
  padding-top: 0.5rem;
}

.btn-pag {
  background: #f8fdfb;
  border: 2px solid #9fe1cb;
  border-radius: 8px;
  padding: 0.45rem 1.1rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #0f6e56;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-pag:hover:not(:disabled) { background: #9fe1cb; color: white; }
.btn-pag:disabled { opacity: 0.4; cursor: not-allowed; }

.pag-info { font-size: 0.92rem; color: #5f6b7a; }

/* ── Modal overlay ── */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
}

.modal {
  background: white;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.18);
  border: 1px solid #c8f0e6;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.97); }
  to   { opacity: 1; transform: scale(1); }
}

.modal-header-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.8rem;
  padding-bottom: 1.2rem;
  border-bottom: 2px solid #e6f7f2;
}

.modal-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
  flex-shrink: 0;
}

.modal h3 {
  color: #0f6e56;
  margin: 0;
  font-size: 1.4rem;
}

.modal-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.1rem 1.5rem;
  margin-bottom: 1.8rem;
}

.modal-field { display: flex; flex-direction: column; gap: 0.35rem; }
.full-width  { grid-column: 1 / -1; }

.field-value { font-size: 1rem; color: #1a202c; }
.required    { color: #e74c3c; }

.button-group { display: flex; gap: 14px; }

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.btn-primary   { background: linear-gradient(90deg, #baf5f2, #47a595); color: white; }
.btn-secondary { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }

.btn-primary:hover,
.btn-secondary:hover { opacity: 0.9; transform: translateY(-2px); }

/* ── Responsivo ── */
@media (max-width: 768px) {
  .psicologos-card { padding: 1.5rem 1rem; }
  .filtros-grid    { grid-template-columns: 1fr; }
  .modal           { padding: 1.8rem 1.2rem; }
  .modal-grid      { grid-template-columns: 1fr; }
  .actions-group   { flex-direction: column; }
  .button-group    { flex-direction: column; }
}
</style>

```

<style scoped>
/* ── Contenedor principal ── */
.psicologos-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

/* ── Card principal ── */
.psicologos-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 1150px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

/* ── Encabezado ── */
.psicologos-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.psicologos-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  flex-shrink: 0;
}

.title-container { flex: 1; }

h2 {
  color: #0f6e56;
  margin: 0 0 0.4rem 0;
  font-size: 1.8rem;
}

.subtitle {
  color: #5f6b7a;
  margin: 0;
  font-size: 1rem;
}

/* ── Filtros ── */
.filtros-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1rem 1.5rem;
  margin-bottom: 1.2rem;
}

.filtro-field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.field-label {
  font-size: 0.82rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: #0f6e56;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: #f8fdfb;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  padding: 0.6rem 1rem;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.search-bar:focus-within {
  border-color: #1d9e75;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

.search-icon { font-size: 1.1rem; flex-shrink: 0; }

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 1rem;
  color: #1a202c;
  outline: none;
}

.clear-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  color: #5f6b7a;
  padding: 0;
  transition: color 0.2s;
}
.clear-btn:hover { color: #e74c3c; }

.field-input {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  font-size: 1rem;
  background: #f8fdfb;
  color: #1a202c;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
  font-family: inherit;
}

.field-input:focus {
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

/* ── Barra de acciones ── */
.barra-acciones {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.2rem;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.conteo-total { font-size: 0.98rem; color: #5f6b7a; }
.conteo-total strong { color: #0f6e56; }

.export-group { display: flex; gap: 0.6rem; }

.btn-export {
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-export:hover { opacity: 0.88; transform: translateY(-1px); }

.btn-excel { background: #d3f5ef; color: #0f6e56; }
.btn-pdf   { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }

/* ── Estado vacío ── */
.estado-info {
  text-align: center;
  padding: 3rem 1rem;
  color: #5f6b7a;
  font-size: 1rem;
}
.empty-icon { font-size: 2.5rem; display: block; margin-bottom: 0.5rem; }

/* ── Tabla ── */
.tabla-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #c8f0e6;
  margin-bottom: 1.2rem;
}

.tabla-datos {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.93rem;
  background: white;
}

.tabla-datos thead tr {
  background: linear-gradient(135deg, #baf5f2, #9fe1cb);
  color: #0f6e56;
  text-align: left;
}

.tabla-datos th {
  padding: 0.85rem 1rem;
  font-weight: 700;
  white-space: nowrap;
}

.tabla-datos tbody tr {
  border-bottom: 1px solid #e6f7f2;
  transition: background 0.15s;
}
.tabla-datos tbody tr:hover { background: #f8fdfb; }

.tabla-datos td {
  padding: 0.8rem 1rem;
  color: #2d3748;
  vertical-align: middle;
}

.td-index  { color: #9fe1cb; font-weight: 700; width: 40px; }
.td-nombre { font-weight: 600; color: #0f6e56; }
.txt-bold  { font-weight: 700; }

/* ── Badges ── */
.badge-estado {
  display: inline-block;
  padding: 0.2rem 0.85rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
  white-space: nowrap;
}
.badge-activo   { background: #c6f6d5; color: #276749; }
.badge-inactivo { background: #fed7d7; color: #9b2c2c; }

/* ── Botones de acción en tabla ── */
.actions-group {
  display: flex;
  gap: 0.4rem;
  flex-wrap: nowrap;
}

.btn-accion {
  border: none;
  border-radius: 8px;
  padding: 0.38rem 0.75rem;
  font-size: 0.82rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-accion:hover { opacity: 0.85; transform: translateY(-1px); }

.btn-ver        { background: #d3f5ef; color: #0f6e56; }
.btn-editar     { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-desactivar { background: #ffd6d6; color: #c53030; }
.btn-activar    { background: #c6f6d5; color: #276749; }

/* ── Paginación ── */
.paginacion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.2rem;
  padding-top: 0.5rem;
}

.btn-pag {
  background: #f8fdfb;
  border: 2px solid #9fe1cb;
  border-radius: 8px;
  padding: 0.45rem 1.1rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #0f6e56;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-pag:hover:not(:disabled) { background: #9fe1cb; color: white; }
.btn-pag:disabled { opacity: 0.4; cursor: not-allowed; }

.pag-info { font-size: 0.92rem; color: #5f6b7a; }

/* ── Modal overlay ── */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 1rem;
}

.modal {
  background: white;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.18);
  border: 1px solid #c8f0e6;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.97); }
  to   { opacity: 1; transform: scale(1); }
}

.modal-header-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.8rem;
  padding-bottom: 1.2rem;
  border-bottom: 2px solid #e6f7f2;
}

.modal-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
  flex-shrink: 0;
}

.modal h3 {
  color: #0f6e56;
  margin: 0;
  font-size: 1.4rem;
}

.modal-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.1rem 1.5rem;
  margin-bottom: 1.8rem;
}

.modal-field { display: flex; flex-direction: column; gap: 0.35rem; }
.full-width  { grid-column: 1 / -1; }

.field-value { font-size: 1rem; color: #1a202c; }
.required    { color: #e74c3c; }

.button-group { display: flex; gap: 14px; }

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.btn-primary   { background: linear-gradient(90deg, #baf5f2, #47a595); color: white; }
.btn-secondary { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }

.btn-primary:hover,
.btn-secondary:hover { opacity: 0.9; transform: translateY(-2px); }

/* ── Responsivo ── */
@media (max-width: 768px) {
  .psicologos-card { padding: 1.5rem 1rem; }
  .filtros-grid    { grid-template-columns: 1fr; }
  .modal           { padding: 1.8rem 1.2rem; }
  .modal-grid      { grid-template-columns: 1fr; }
  .actions-group   { flex-direction: column; }
  .button-group    { flex-direction: column; }
}
</style>