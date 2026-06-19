<template>
  <div class="buscar-cita-container">
    <div class="buscar-cita-card">

      <!-- Encabezado con logo -->
      <div class="buscar-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="buscar-logo" />
        <div class="title-container">
          <h2>Buscar Citas</h2>
          <p class="subtitle">Consulta, filtra y gestiona todas las citas del sistema.</p>
        </div>
      </div>

      <!-- Barra de búsqueda -->
      <div class="search-bar">
        <span class="search-icon">🔍</span>
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por nombre del paciente..."
          class="search-input"
          @input="filtrarCitas"
        />
        <button v-if="busqueda" class="clear-btn" @click="limpiarBusqueda">✕</button>
      </div>

      <!-- Estado: Cargando -->
      <div v-if="cargando" class="estado-info">
        <span>⏳</span> Cargando citas...
      </div>

      <!-- Estado: Error -->
      <div v-else-if="error" class="estado-error">
        ⚠️ {{ error }}
      </div>

      <!-- Estado: Sin resultados -->
      <div v-else-if="citasFiltradas.length === 0" class="estado-info">
        <span class="empty-icon">📭</span>
        <p v-if="busqueda">No se encontraron citas para <strong>"{{ busqueda }}"</strong>.</p>
        <p v-else>No hay citas registradas en el sistema aún.</p>
      </div>

      <!-- Tabla de citas -->
      <div v-else class="tabla-wrapper">
        <table class="tabla-citas">
          <thead>
            <tr>
              <th>#</th>
              <th>Paciente</th>
              <th>Fecha</th>
              <th>Hora</th>
              <th>Motivo</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(cita, index) in citasFiltradas" :key="cita.id">
              <td class="td-index">{{ index + 1 }}</td>
              <td class="td-paciente">{{ cita.nombrePaciente }}</td>
              <td>{{ formatearFecha(cita.fecha) }}</td>
              <td>{{ cita.hora }}</td>
              <td class="td-motivo">{{ cita.motivo || '—' }}</td>
              <td>
                <span :class="['badge-estado', estadoClase(cita.estado)]">
                  {{ cita.estado }}
                </span>
              </td>
              <td class="td-acciones">
                <button class="btn-accion btn-ver"      @click="verCita(cita)"          title="Ver detalle">👁 Ver</button>
                <button class="btn-accion btn-editar"   @click="modificarCita(cita)"    title="Modificar">✏️ Modificar</button>
                <button class="btn-accion btn-eliminar" @click="confirmarEliminar(cita)" title="Eliminar">🗑 Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!-- a ver si sube -->
    <!-- ── Modal: Ver detalle ── -->
    <div v-if="citaSeleccionada && modalTipo === 'ver'" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <h3>Detalle de la Cita</h3>
        </div>
        <div class="modal-grid">
          <div class="modal-field">
            <span class="field-label">Paciente</span>
            <span class="field-value">{{ citaSeleccionada.nombrePaciente }}</span>
          </div>
          <div class="modal-field">
            <span class="field-label">Fecha</span>
            <span class="field-value">{{ formatearFecha(citaSeleccionada.fecha) }}</span>
          </div>
          <div class="modal-field">
            <span class="field-label">Hora</span>
            <span class="field-value">{{ citaSeleccionada.hora }}</span>
          </div>
          <div class="modal-field">
            <span class="field-label">Estado</span>
            <span :class="['badge-estado', estadoClase(citaSeleccionada.estado)]">
              {{ citaSeleccionada.estado }}
            </span>
          </div>
          <div class="modal-field full-width">
            <span class="field-label">Motivo</span>
            <span class="field-value">{{ citaSeleccionada.motivo || 'Sin motivo registrado' }}</span>
          </div>
          <div class="modal-field full-width">
            <span class="field-label">Notas</span>
            <span class="field-value">{{ citaSeleccionada.notas || 'Sin notas adicionales' }}</span>
          </div>
        </div>
        <div class="button-group">
          <button class="btn-secondary" @click="cerrarModal">Cerrar</button>
        </div>
      </div>
    </div>

    <!-- ── Modal: Modificar ── -->
    <div v-if="citaSeleccionada && modalTipo === 'editar'" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <h3>Modificar Cita</h3>
        </div>
        <div class="modal-grid">
          <div class="modal-field">
            <label class="field-label" for="editFecha">Fecha <span class="required">*</span></label>
            <input id="editFecha" v-model="citaEditada.fecha" type="date" class="field-input" />
          </div>
          <div class="modal-field">
            <label class="field-label" for="editHora">Hora <span class="required">*</span></label>
            <input id="editHora" v-model="citaEditada.hora" type="time" class="field-input" />
          </div>
          <div class="modal-field">
            <label class="field-label" for="editEstado">Estado</label>
            <select id="editEstado" v-model="citaEditada.estado" class="field-input">
              <option value="Pendiente">Pendiente</option>
              <option value="Confirmada">Confirmada</option>
              <option value="Cancelada">Cancelada</option>
              <option value="Completada">Completada</option>
            </select>
          </div>
          <div class="modal-field full-width">
            <label class="field-label" for="editMotivo">Motivo</label>
            <input id="editMotivo" v-model="citaEditada.motivo" type="text" class="field-input" />
          </div>
          <div class="modal-field full-width">
            <label class="field-label" for="editNotas">Notas</label>
            <textarea id="editNotas" v-model="citaEditada.notas" class="field-input field-textarea" rows="3" />
          </div>
        </div>
        <div class="button-group">
          <button class="btn-primary" @click="guardarCambios" :disabled="guardando">
            {{ guardando ? 'Guardando...' : 'Guardar Cambios' }}
          </button>
          <button class="btn-secondary" @click="cerrarModal" :disabled="guardando">Cancelar</button>
        </div>
      </div>
    </div>

    <!-- ── Modal: Confirmar eliminación ── -->
    <div v-if="citaSeleccionada && modalTipo === 'eliminar'" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal modal-small">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <h3>Confirmar Eliminación</h3>
        </div>
        <p class="modal-mensaje">
          ¿Estás seguro que deseas eliminar la cita de
          <strong>{{ citaSeleccionada.nombrePaciente }}</strong> del
          <strong>{{ formatearFecha(citaSeleccionada.fecha) }}</strong>?
          Esta acción no se puede deshacer.
        </p>
        <div class="button-group">
          <button class="btn-danger" @click="eliminarCita" :disabled="eliminando">
            {{ eliminando ? 'Eliminando...' : 'Sí, eliminar' }}
          </button>
          <button class="btn-secondary" @click="cerrarModal" :disabled="eliminando">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// ── Tipos ──────────────────────────────────────────────────────────────────
interface Cita {
  id: number
  nombrePaciente: string
  fecha: string
  hora: string
  motivo?: string
  notas?: string
  estado: 'Pendiente' | 'Confirmada' | 'Cancelada' | 'Completada'
}

// ── Estado ─────────────────────────────────────────────────────────────────
const citas            = ref<Cita[]>([])
const citasFiltradas   = ref<Cita[]>([])
const busqueda         = ref('')
const cargando         = ref(false)
const guardando        = ref(false)
const eliminando       = ref(false)
const error            = ref('')

const modalTipo        = ref<'ver' | 'editar' | 'eliminar' | null>(null)
const citaSeleccionada = ref<Cita | null>(null)
const citaEditada      = ref<Partial<Cita>>({})

// ── API ────────────────────────────────────────────────────────────────────
const cargarCitas = async () => {
  cargando.value = true
  error.value = ''
  try {
    const response = await fetch('http://localhost:8080/api/citas')
    if (!response.ok) throw new Error('Error al obtener las citas del servidor.')
    const data: Cita[] = await response.json()
    citas.value = data
    citasFiltradas.value = data
  } catch (e: any) {
    error.value = e.message || 'No se pudo conectar con el servidor.'
  } finally {
    cargando.value = false
  }
}

// ── Búsqueda ───────────────────────────────────────────────────────────────
const filtrarCitas = () => {
  const termino = busqueda.value.toLowerCase().trim()
  citasFiltradas.value = termino
    ? citas.value.filter(c => c.nombrePaciente.toLowerCase().includes(termino))
    : [...citas.value]
}

const limpiarBusqueda = () => {
  busqueda.value = ''
  citasFiltradas.value = [...citas.value]
}

// ── Modales ────────────────────────────────────────────────────────────────
const verCita = (cita: Cita) => {
  citaSeleccionada.value = cita
  modalTipo.value = 'ver'
}

const modificarCita = (cita: Cita) => {
  citaSeleccionada.value = cita
  citaEditada.value = { ...cita }
  modalTipo.value = 'editar'
}

const confirmarEliminar = (cita: Cita) => {
  citaSeleccionada.value = cita
  modalTipo.value = 'eliminar'
}

const cerrarModal = () => {
  citaSeleccionada.value = null
  modalTipo.value = null
  citaEditada.value = {}
}

// ── Acciones ───────────────────────────────────────────────────────────────
const guardarCambios = async () => {
  if (!citaSeleccionada.value) return
  guardando.value = true
  try {
    const response = await fetch(`http://localhost:8080/api/citas/${citaSeleccionada.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(citaEditada.value),
    })
    if (!response.ok) throw new Error('Error al modificar la cita.')
    await cargarCitas()
    cerrarModal()
    alert('Cita actualizada correctamente.')
  } catch (e: any) {
    alert(e.message)
  } finally {
    guardando.value = false
  }
}

const eliminarCita = async () => {
  if (!citaSeleccionada.value) return
  eliminando.value = true
  try {
    const response = await fetch(`http://localhost:8080/api/citas/${citaSeleccionada.value.id}`, {
      method: 'DELETE',
    })
    if (!response.ok) throw new Error('Error al eliminar la cita.')
    await cargarCitas()
    cerrarModal()
    alert('Cita eliminada correctamente.')
  } catch (e: any) {
    alert(e.message)
  } finally {
    eliminando.value = false
  }
}

// ── Utilidades ─────────────────────────────────────────────────────────────
const formatearFecha = (fecha: string) => {
  if (!fecha) return '—'
  const [year, month, day] = fecha.split('-')
  return `${day}/${month}/${year}`
}

const estadoClase = (estado: string) => {
  const mapa: Record<string, string> = {
    Pendiente:  'estado-pendiente',
    Confirmada: 'estado-confirmada',
    Cancelada:  'estado-cancelada',
    Completada: 'estado-completada',
  }
  return mapa[estado] ?? ''
}

onMounted(() => cargarCitas())
</script>

<style scoped>
/* ── Contenedor principal ── */
.buscar-cita-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

/* ── Card principal (mismo estilo que HistoriaView) ── */
.buscar-cita-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 1050px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

/* ── Encabezado con logo ── */
.buscar-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.buscar-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  flex-shrink: 0;
}

.title-container {
  flex: 1;
  text-align: left;
}

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

/* ── Barra de búsqueda ── */
.search-bar {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: #f8fdfb;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  padding: 0.6rem 1rem;
  margin-bottom: 1.8rem;
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
  line-height: 1;
  transition: color 0.2s;
}
.clear-btn:hover { color: #e74c3c; }

/* ── Estados vacío / cargando / error ── */
.estado-info,
.estado-error {
  text-align: center;
  padding: 3rem 1rem;
  color: #5f6b7a;
  font-size: 1rem;
}
.estado-error { color: #e74c3c; }
.empty-icon   { font-size: 2.5rem; display: block; margin-bottom: 0.5rem; }

/* ── Tabla ── */
.tabla-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #c8f0e6;
}

.tabla-citas {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.93rem;
}

.tabla-citas thead tr {
  background: linear-gradient(135deg, #baf5f2, #9fe1cb);
  color: #0f6e56;
  text-align: left;
}

.tabla-citas th {
  padding: 0.85rem 1rem;
  font-weight: 700;
  white-space: nowrap;
}

.tabla-citas tbody tr {
  border-bottom: 1px solid #e6f7f2;
  transition: background 0.15s;
}

.tabla-citas tbody tr:hover { background-color: #f8fdfb; }

.tabla-citas td {
  padding: 0.8rem 1rem;
  color: #2d3748;
  vertical-align: middle;
}

.td-index   { color: #9fe1cb; font-weight: 700; width: 40px; }
.td-paciente { font-weight: 600; color: #0f6e56; }
.td-motivo  {
  max-width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #5f6b7a;
}

/* ── Badges de estado ── */
.badge-estado {
  display: inline-block;
  padding: 0.2rem 0.75rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
  white-space: nowrap;
}
.estado-pendiente  { background: #fefcbf; color: #b7791f; }
.estado-confirmada { background: #c6f6d5; color: #276749; }
.estado-cancelada  { background: #fed7d7; color: #9b2c2c; }
.estado-completada { background: #bee3f8; color: #2b6cb0; }

/* ── Botones de acción en tabla ── */
.td-acciones {
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

.btn-ver      { background: #c6f6d5; color: #276749; }
.btn-editar   { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-eliminar { background: #fed7d7; color: #9b2c2c; }

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

/* ── Modal card (mismo estilo que HistoriaView card) ── */
.modal {
  background: white;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 580px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.18);
  border: 1px solid #c8f0e6;
  animation: fadeIn 0.2s ease;
}

.modal-small { max-width: 440px; }

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.97); }
  to   { opacity: 1; transform: scale(1); }
}

/* ── Encabezado del modal ── */
.modal-header-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.8rem;
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
  font-size: 1.5rem;
}

/* ── Grid de campos ── */
.modal-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.1rem 1.5rem;
  margin-bottom: 1.8rem;
}

.modal-field {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.full-width { grid-column: 1 / -1; }

.field-label {
  font-size: 0.82rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: #0f6e56;
}

.required { color: #e74c3c; }

.field-value {
  font-size: 1rem;
  color: #1a202c;
}

/* Inputs del modal (mismo estilo que HistoriaView) */
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

.field-textarea { resize: vertical; }

.modal-mensaje {
  color: #5f6b7a;
  line-height: 1.6;
  margin: 0 0 1.8rem 0;
  font-size: 1rem;
}

/* ── Botones de modal (mismo gradiente que HistoriaView) ── */
.button-group {
  display: flex;
  gap: 14px;
  margin-top: 0.5rem;
}

.btn-primary,
.btn-secondary,
.btn-danger {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.btn-primary {
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: white;
}

.btn-secondary {
  background: linear-gradient(90deg, #acadd6, #7e80da);
  color: white;
}

.btn-danger {
  background: linear-gradient(90deg, #fbb6b6, #e53e3e);
  color: white;
}

.btn-primary:hover:not(:disabled),
.btn-secondary:hover:not(:disabled),
.btn-danger:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-2px);
}

.btn-primary:disabled,
.btn-secondary:disabled,
.btn-danger:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* ── Responsivo ── */
@media (max-width: 768px) {
  .buscar-cita-card { padding: 1.5rem 1rem; }
  .modal             { padding: 1.8rem 1.2rem; }
  .modal-grid        { grid-template-columns: 1fr; }
  .td-acciones       { flex-direction: column; }
  .button-group      { flex-direction: column; }
}
</style>