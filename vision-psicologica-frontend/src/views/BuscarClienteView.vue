<template>
  <div class="cliente-container">
    <div class="cliente-card">

      <!-- Encabezado con logo (mismo estilo que HistoriaView) -->
      <div class="cliente-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="cliente-logo" />
        <div class="title-container">
          <h2>Búsqueda de cliente</h2>
          <p class="subtitle">Consulta los datos básicos de los pacientes registrados en el sistema por documento, nombre o email</p>
        </div>
      </div>

      <!-- Barra de búsqueda -->
      <div class="search-bar">
        <span class="search-icon">🔍</span>
        <input
          id="buscarCliente"
          v-model="terminoBusqueda"
          type="text"
          placeholder="Digite la cédula, el nombre o el correo del paciente..."
          class="search-input"
        />
        <button v-if="terminoBusqueda" class="clear-btn" @click="limpiarBusqueda">✕</button>
      </div>

      <!-- Meta resultados -->
      <div class="resultados-meta">
        Clientes Encontrados: <strong>{{ clientesFiltrados.length }}</strong>
      </div>

      <!-- Tabla de resultados -->
      <div class="tabla-wrapper" v-if="clientesFiltrados.length > 0">
        <table class="tabla-datos">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Identificación</th>
              <th>Email</th>
              <th>Dirección</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cliente in clientesFiltrados" :key="cliente.id">
              <td class="td-nombre">{{ cliente.nombre }}</td>
              <td class="txt-bold">{{ cliente.identificacion }}</td>
              <td>{{ cliente.correoElectronico }}</td>
              <td>{{ cliente.direccion || 'No registrada' }}</td>
              <td>
                <div class="actions-group">
                  <button class="btn-accion btn-edit" @click="abrirEdicion(cliente)">Editar</button>
                  <button class="btn-accion btn-history" @click="abrirHistorialCitas(cliente)">Historial</button>
                  <button class="btn-accion btn-schedule" @click="abrirAgendar(cliente)">Agendar</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="estado-info" v-else>
        <span class="empty-icon">📭</span>
        <p>No se encontraron registros que coincidan con la búsqueda.</p>
      </div>
    </div>

    <!-- ── Modal: Editar Cliente ── -->
    <div v-if="clienteEditando" class="modal-overlay" @click.self="clienteEditando = null">
      <div class="modal">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <h3>Editar Cliente</h3>
        </div>

        <div class="modal-grid">
          <div class="modal-field full-width">
            <label class="field-label">Nombre</label>
            <input v-model="clienteEditando.nombre" class="field-input" />
          </div>
          <div class="modal-field full-width">
            <label class="field-label">Identificación</label>
            <input v-model="clienteEditando.identificacion" class="field-input" />
          </div>
          <div class="modal-field full-width">
            <label class="field-label">Email</label>
            <input v-model="clienteEditando.correoElectronico" class="field-input" />
          </div>
          <div class="modal-field full-width">
            <label class="field-label">Dirección</label>
            <input v-model="clienteEditando.direccion" class="field-input" />
          </div>
        </div>

        <div class="button-group">
          <button class="btn-primary" @click="guardarCambios">Guardar</button>
          <button class="btn-secondary" @click="clienteEditando = null">Cancelar</button>
        </div>
      </div>
    </div>

    <!-- ── Modal: Historial de Citas ── -->
    <div v-if="mostrarModalHistorial" class="modal-overlay" @click.self="cerrarHistorial">
      <div class="modal modal-large">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <div>
            <h3>Historial de Citas</h3>
            <span class="paciente-tag">Paciente: <strong>{{ clienteSeleccionado?.nombre }}</strong></span>
          </div>
        </div>

        <div class="citas-container">
          <div class="tabla-wrapper" v-if="historialCitas.length > 0">
            <table class="tabla-datos tabla-citas">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Psicólogo que Atendió</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="cita in historialCitas" :key="cita.id">
                  <td>{{ formatearFecha(cita.fechaInicio) }}</td>
                  <td class="txt-bold">{{ formatearHora(cita.fechaInicio) }} - {{ formatearHora(cita.fechaFin) }}</td>
                  <td>{{ cita.psicologo?.nombre || 'No asignado' }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="estado-info" v-else>
            <span class="empty-icon">📭</span>
            <p>No se encontraron citas registradas.</p>
          </div>
        </div>

        <div class="button-group close-action">
          <button class="btn-secondary" @click="cerrarHistorial">Cerrar Historial</button>
        </div>
      </div>
    </div>

    <!-- ── Modal: Agendar Cita ── -->
    <div v-if="clienteParaAgendar" class="modal-overlay" @click.self="cerrarAgendar">
      <div class="modal">
        <div class="modal-header-row">
          <img src="@/assets/mariposa.png" alt="logo" class="modal-logo" />
          <div>
            <h3>Agendar Cita</h3>
            <span class="paciente-tag">Paciente: <strong>{{ clienteParaAgendar.nombre }}</strong></span>
          </div>
        </div>

        <div class="modal-grid">
          <div class="modal-field full-width">
            <label class="field-label">Psicólogo <span class="required">*</span></label>
            <select v-model="formCita.psicologoId" class="field-input">
              <option value="" disabled>Seleccione un psicólogo...</option>
              <option v-for="psic in psicologos" :key="psic.id" :value="psic.id">
                {{ psic.nombre }}
              </option>
            </select>
          </div>
          <div class="modal-field">
            <label class="field-label">Fecha inicio <span class="required">*</span></label>
            <input type="datetime-local" v-model="formCita.fechaInicio" class="field-input" />
          </div>
          <div class="modal-field">
            <label class="field-label">Fecha fin <span class="required">*</span></label>
            <input type="datetime-local" v-model="formCita.fechaFin" class="field-input" />
          </div>
        </div>

        <div class="button-group">
          <button class="btn-primary" @click="confirmarAgendar">Confirmar</button>
          <button class="btn-secondary" @click="cerrarAgendar">Cancelar</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

const terminoBusqueda = ref('')
const listaClientesReal = ref<any[]>([])
const clienteEditando = ref<any | null>(null)

// Variables de estado para la HU-23
const mostrarModalHistorial = ref(false)
const clienteSeleccionado = ref<any | null>(null)
const historialCitas = ref<any[]>([])

// 3. VARIABLES PARA AGENDAR CITA
const clienteParaAgendar = ref<any | null>(null)
const psicologos = ref<any[]>([])
const formCita = ref({
  psicologoId: '',
  fechaInicio: '',
  fechaFin: ''
})

const clientesFiltrados = computed(() => {
  const query = terminoBusqueda.value.trim().toLowerCase()
  if (!query) return listaClientesReal.value
  return listaClientesReal.value.filter(cliente => {
    return cliente.identificacion?.toLowerCase().includes(query) ||
           cliente.nombre?.toLowerCase().includes(query) ||
           cliente.correoElectronico?.toLowerCase().includes(query)
  })
})

const obtenerClientesDeBackend = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/clientes')
    if (!response.ok) throw new Error('Error al conectar')
    listaClientesReal.value = await response.json()
  } catch (error) {
    console.error('Error:', error)
  }
}

// 4. LÓGICA PARA CARGAR PSICÓLOGOS EN EL ONMOUNTED
onMounted(async () => {
  await obtenerClientesDeBackend()
  try {
    const resPsicologos = await fetch('http://localhost:8080/usuario')
    if (resPsicologos.ok) {
      psicologos.value = await resPsicologos.json()
    }
  } catch (error) {
    console.error('Error al cargar psicólogos:', error)
  }
})

const abrirEdicion = (cliente: any) => {
  clienteEditando.value = JSON.parse(JSON.stringify(cliente))
} 

const abrirHistorialCitas = async (cliente: any) => {
  clienteSeleccionado.value = cliente
  try {
    const response = await fetch(`http://localhost:8080/citas/cliente/${cliente.id}`)
    if (response.ok) {
      historialCitas.value = await response.json()
      historialCitas.value.sort((a, b) => new Date(b.fechaInicio).getTime() - new Date(a.fechaInicio).getTime())
      mostrarModalHistorial.value = true
    } else {
      alert("No se pudo obtener el historial de este paciente.");
    }
  } catch (error) {
    console.error("Error al obtener citas:", error)
    alert("Error de conexión con el servidor de citas.")
  }
}

const cerrarHistorial = () => {
  mostrarModalHistorial.value = false
  clienteSeleccionado.value = null
  historialCitas.value = []
}

// 5. MÉTODOS PARA AGENDAR CITA
const abrirAgendar = (cliente: any) => {
  clienteParaAgendar.value = cliente
}

const cerrarAgendar = () => {
  clienteParaAgendar.value = null
  formCita.value = { psicologoId: '', fechaInicio: '', fechaFin: '' }
}

const confirmarAgendar = async () => {
  if (!formCita.value.psicologoId || !formCita.value.fechaInicio || !formCita.value.fechaFin) {
    alert("Por favor completa todos los campos.")
    return
  }

  const payload = {
    cliente: { id: clienteParaAgendar.value.id },
    psicologo: { id: Number(formCita.value.psicologoId) },
    fechaInicio: formCita.value.fechaInicio,
    fechaFin: formCita.value.fechaFin
  }

  try {
    const res = await fetch('http://localhost:8080/api/citas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    
    if (res.ok) {
      alert("¡Cita agendada correctamente!")
      cerrarAgendar()
    } else {
      const data = await res.json()
      alert(`Error al agendar: ${data.error || 'Verifica la disponibilidad'}`)
    }
  } catch (error) {
    console.error("Error conectando al servidor:", error)
    alert("Hubo un problema de conexión con el servidor.")
  }
}

const formatearFecha = (fechaStr: string) => {
  if (!fechaStr) return ''
  const fecha = new Date(fechaStr)
  return fecha.toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })
}

const formatearHora = (fechaStr: string) => {
  if (!fechaStr) return ''
  const fecha = new Date(fechaStr)
  return fecha.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', hour12: true })
}

const guardarCambios = async () => {
  try {
    const payload = {
      nombre: clienteEditando.value.nombre,
      identificacion: clienteEditando.value.identificacion,
      correoElectronico: clienteEditando.value.correoElectronico,
      direccion: clienteEditando.value.direccion
    };

    const response = await fetch(`http://localhost:8080/api/clientes/${clienteEditando.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      alert("¡Cliente actualizado con éxito!");
      clienteEditando.value = null;
      await obtenerClientesDeBackend();
    } else {
      const errorText = await response.text();
      alert("Error al actualizar: " + errorText);
    }
  } catch (error) {
    console.error("Error al guardar:", error);
    alert("No se pudo conectar con el servidor.");
  }
}

const limpiarBusqueda = () => terminoBusqueda.value = ''
</script>

<style scoped>
/* ── Contenedor principal ── */
.cliente-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

/* ── Card principal (mismo estilo que HistoriaView) ── */
.cliente-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 1100px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

/* ── Encabezado con logo ── */
.cliente-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.cliente-logo {
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
  margin-bottom: 1rem;
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

/* ── Meta resultados ── */
.resultados-meta {
  color: #5f6b7a;
  font-size: 0.95rem;
  margin-bottom: 1.2rem;
}
.resultados-meta strong { color: #0f6e56; }

/* ── Estados vacío ── */
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

.tabla-datos tbody tr:hover { background-color: #f8fdfb; }

.tabla-datos td {
  padding: 0.8rem 1rem;
  color: #2d3748;
  vertical-align: middle;
}

.td-nombre { font-weight: 600; color: #0f6e56; }
.txt-bold { font-weight: 700; }

/* ── Botones de acción en tabla ── */
.actions-group {
  display: flex;
  gap: 0.4rem;
  flex-wrap: nowrap;
}

.btn-accion {
  border: none;
  border-radius: 8px;
  padding: 0.4rem 0.85rem;
  font-size: 0.82rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s, transform 0.1s;
}
.btn-accion:hover { opacity: 0.88; transform: translateY(-1px); }

.btn-edit      { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-history   { background: linear-gradient(90deg, #0f6e56, #134e3a); color: white; }
.btn-schedule  { background: linear-gradient(90deg, #baf5f2, #47a595); color: white; }

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
  max-width: 480px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.18);
  border: 1px solid #c8f0e6;
  animation: fadeIn 0.2s ease;
}

.modal-large {
  max-width: 700px;
  max-height: 85vh;
  overflow-y: auto;
}

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
  margin: 0 0 0.3rem 0;
  font-size: 1.4rem;
}

.paciente-tag {
  background-color: #e6f7f2;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.88rem;
  color: #0f6e56;
  display: inline-block;
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

/* ── Historial dentro del modal ── */
.citas-container { margin-bottom: 1.5rem; }
.tabla-citas th { background: linear-gradient(135deg, #baf5f2, #9fe1cb); color: #0f6e56; }

/* ── Botones de modal (mismo gradiente que HistoriaView) ── */
.button-group {
  display: flex;
  gap: 14px;
  margin-top: 0.5rem;
}

.close-action {
  justify-content: flex-end;
}

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

.btn-primary {
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: white;
}

.btn-secondary {
  background: linear-gradient(90deg, #acadd6, #7e80da);
  color: white;
}

.close-action .btn-secondary {
  flex: none;
  padding: 12px 28px;
}

.btn-primary:hover,
.btn-secondary:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

/* ── Responsivo ── */
@media (max-width: 768px) {
  .cliente-card { padding: 1.5rem 1rem; }
  .modal         { padding: 1.8rem 1.2rem; }
  .modal-grid    { grid-template-columns: 1fr; }
  .actions-group { flex-direction: column; }
  .button-group  { flex-direction: column; }
  .close-action .btn-secondary { width: 100%; }
}
</style>