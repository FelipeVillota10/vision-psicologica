<template>
  <div class="modulo-container">
    <div class="modulo-header">
      <h2>Búsqueda de cliente</h2>
      <p class="subtitle">Consulta los datos básicos de los pacientes registrados en el sistema por documento, nombre o email</p>
    </div>

    <div class="search-box">
      <div class="form-group">
        <label for="buscarCliente">Buscar Cliente (Documento, Nombre o Email):</label>
        <div class="search-input-group">
          <input
            id="buscarCliente"
            v-model="terminoBusqueda"
            type="text"
            placeholder="Digite la cédula, el nombre o el correo del paciente..."
          />
          <button class="btn-clear" @click="limpiarBusqueda">Limpiar</button>
        </div>
      </div>
    </div>

    <div class="resultados-section">
      <div class="resultados-meta">
        <span>Clientes Encontrados: <strong>{{ clientesFiltrados.length }}</strong></span>
      </div>

      <table class="tabla-datos" v-if="clientesFiltrados.length > 0">
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
            <td>{{ cliente.nombre }}</td>
            <td class="txt-bold">{{ cliente.identificacion }}</td>
            <td>{{ cliente.correoElectronico }}</td>
            <td>{{ cliente.direccion || 'No registrada' }}</td>
            <td>
              <div class="actions-group">
                <button class="btn-edit" @click="abrirEdicion(cliente)">Editar</button>
                <button class="btn-history" @click="abrirHistorialCitas(cliente)">Historial</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="no-resultados" v-else>
        <p>No se encontraron registros que coincidan con la búsqueda.</p>
      </div>
    </div>

    <!-- MODAL: EDITAR CLIENTE -->
    <div v-if="clienteEditando" class="modal-overlay">
      <div class="modal-content">
        <h3>Editar Cliente</h3>
        <div class="form-edit">
          <label>Nombre:</label>
          <input v-model="clienteEditando.nombre" />
          <label>Identificación:</label>
          <input v-model="clienteEditando.identificacion" />
          <label>Email:</label>
          <input v-model="clienteEditando.correoElectronico" />
          <label>Dirección:</label>
          <input v-model="clienteEditando.direccion" />
        </div>
        <div class="modal-actions">
          <button class="btn-search" @click="guardarCambios">Guardar</button>
          <button class="btn-clear" @click="clienteEditando = null">Cancelar</button>
        </div>
      </div>
    </div>

    <!-- MODAL: HU-23 HISTORIAL DE CITAS DEL PACIENTE -->
    <div v-if="mostrarModalHistorial" class="modal-overlay">
      <div class="modal-content modal-large">
        <div class="modal-header-citas">
          <h3>Historial de Citas</h3>
          <span class="paciente-tag">Paciente: <strong>{{ clienteSeleccionado?.nombre }}</strong></span>
        </div>
        
        <div class="citas-container">
          <!-- Si el paciente tiene citas registradas -->
          <table class="tabla-datos tabla-citas" v-if="historialCitas.length > 0">
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

          <!-- Mensaje amigable HU-23 si es un paciente nuevo o sin citas -->
          <div class="no-citas" v-else>
            <p>No se encontraron citas registradas.</p>
          </div>
        </div>

        <div class="modal-actions close-action">
          <button class="btn-close-modal" @click="cerrarHistorial">Cerrar Historial</button>
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

const abrirEdicion = (cliente: any) => {
  clienteEditando.value = JSON.parse(JSON.stringify(cliente))
} 

// Métodos para la HU-23 (Cargar citas desde tu CitaController)
const abrirHistorialCitas = async (cliente: any) => {
  clienteSeleccionado.value = cliente
  try {
    // Apunta al puerto 8080 y al endpoint /citas/cliente/{id} de tu controlador de Spring Boot
    const response = await fetch(`http://localhost:8080/citas/cliente/${cliente.id}`)
    if (response.ok) {
      historialCitas.value = await response.json()
      // Ordenamos las citas cronológicamente (de la más reciente a la más antigua)
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

// Funciones helpers para formatear fechas y horas estéticamente
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

onMounted(obtenerClientesDeBackend)
</script>

<style scoped>
.modulo-container { padding: 1.5rem; }
.modulo-header h2 { color: #344a73; }
.search-box { background-color: #f4f5f9; padding: 1.25rem; border-radius: 10px; margin-bottom: 1.5rem; }
.search-input-group { display: flex; gap: 0.75rem; }
input { flex: 1; padding: 10px; border: 1.5px solid #b8b9e0; border-radius: 8px; }
button { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; }
.btn-search { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-clear { background: linear-gradient(90deg, #baf5f2, #47a595); color: rgb(253, 253, 253); }
.tabla-datos { width: 100%; border-collapse: collapse; background: white; }
.tabla-datos th, .tabla-datos td { padding: 12px; border-bottom: 1px solid #e2e4f0; text-align: left; }

.actions-group { display: flex; gap: 0.5rem; }
.btn-edit { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; padding: 5px 12px; font-size: 0.8rem; }
/* Estilo del nuevo botón combinado con la paleta */
.btn-history { background: linear-gradient(90deg, #344a73, #1e2d4a); color: white; padding: 5px 12px; font-size: 0.8rem; }

.modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.6); display:flex; justify-content:center; align-items:center; z-index: 1000; }
.modal-content { background: white; padding: 2rem; border-radius: 12px; width: 380px; box-shadow: 0 4px 20px rgba(0,0,0,0.15); }
.modal-large { width: 650px; max-height: 80vh; overflow-y: auto; }
.form-edit { display: flex; flex-direction: column; gap: 10px; margin-bottom: 1rem; }
.modal-actions { display: flex; gap: 10px; }

/* Estilos especiales para el Historial de Citas */
.modal-header-citas { margin-bottom: 1.5rem; border-bottom: 2px solid #f4f5f9; padding-bottom: 0.75rem; }
.modal-header-citas h3 { color: #344a73; margin: 0 0 5px 0; }
.paciente-tag { background-color: #e2e4f0; padding: 4px 10px; border-radius: 6px; font-size: 0.9rem; color: #1e2d4a; }
.citas-container { margin-bottom: 1.5rem; }
.tabla-citas th { background-color: #f4f5f9; color: #344a73; font-weight: bold; }
.no-citas { text-align: center; padding: 2rem; background-color: #fbfbfd; border-radius: 8px; color: #7e80da; font-weight: 500; }
.close-action { justify-content: flex-end; }
.btn-close-modal { background: #e2e4f0; color: #344a73; }
.btn-close-modal:hover { background: #d3d5e4; }
.txt-bold { font-weight: bold; }
</style>