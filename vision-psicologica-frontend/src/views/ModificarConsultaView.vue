<template>
  <div class="consultation-container">
    <div v-if="!editando" class="consultation-card wide-card">
      <div class="consultation-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="consultation-logo" />
        <div class="title-container">
          <h2>Listado de Consultas Clínicas</h2>
          <p class="subtitle">Selecciona una consulta de la lista para modificar su información.</p>
        </div>
      </div>

      <div class="form-group">
        <input
          type="text"
          v-model="busquedaTabla"
          placeholder="Buscar por motivo, diagnóstico o ID..."
          class="search-input"
        />
      </div>

      <div v-if="cargandoConsultas" class="loading-state">
        <p>Cargando historial de consultas...</p>
      </div>

      <div v-else-if="consultasFiltradasTabla.length === 0" class="empty-state">
        <p>No se encontraron consultas registradas.</p>
      </div>

      <div v-else class="table-responsive">
        <table class="consultas-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Fecha</th>
              <th>Motivo de Consulta</th>
              <th>Diagnóstico</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="consulta in consultasFiltradasTabla" :key="consulta.id">
              <td>
                <strong>#{{ consulta.id }}</strong>
              </td>
              <td>{{ formatearFecha(consulta.fechaConsulta) }}</td>
              <td class="truncate-text">{{ consulta.motivoConsulta || 'Sin motivo' }}</td>
              <td class="truncate-text">{{ consulta.diagnostico || 'Sin diagnóstico' }}</td>
              <td>
                <button type="button" class="btn-table-edit" @click="seleccionarConsulta(consulta)">
                  Modificar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-else class="consultation-card">
      <div class="consultation-header">
        <button type="button" class="btn-back" @click="cancelarEdicion">← Volver a la lista</button>
        <div class="title-container">
          <h2>Modificando Consulta #{{ consultaIdActual }}</h2>
          <p class="subtitle">Actualiza la información clínica necesaria.</p>
        </div>
      </div>

      <form @submit.prevent="modificarConsulta">
        <div class="form-group">
          <label>Psicólogo que realizó la consulta <span class="required">*</span></label>
          <input
            type="text"
            v-model="busquedaPsicologo"
            placeholder="Buscar psicólogo..."
            class="search-input"
          />
          <select v-model="form.psicologoId" required>
            <option value="" disabled>Seleccione un psicólogo</option>
            <option
              v-for="psicologo in psicologosFiltrados"
              :key="psicologo.id"
              :value="psicologo.id"
            >
              {{ psicologo.nombre }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Cliente de la consulta <span class="required">*</span></label>
          <input
            type="text"
            v-model="busquedaCliente"
            placeholder="Buscar cliente..."
            class="search-input"
          />
          <select v-model="form.clienteId" required>
            <option value="" disabled>Seleccione un cliente</option>
            <option v-for="cliente in clientesFiltrados" :key="cliente.id" :value="cliente.id">
              {{ cliente.nombre }} ({{ cliente.identificacion }})
            </option>
          </select>

          <p v-if="cargandoHistoria" class="info-status loading">Buscando historia clínica...</p>
          <p v-else-if="historiaClinica" class="info-status success">
            ✓ Historia Clínica N° {{ historiaClinica.id }} vinculada automáticamente.
          </p>
        </div>

        <div class="form-group">
          <label>Fecha y hora de la consulta <span class="required">*</span></label>
          <input type="datetime-local" v-model="form.fechaConsulta" required />
        </div>

        <div class="form-group">
          <label>Nuevo motivo de la consulta</label>
          <textarea v-model="form.motivoConsulta" rows="4" placeholder="Ingrese el motivo..." />
        </div>

        <div class="form-group">
          <label>Nuevo examen mental</label>
          <textarea
            v-model="form.examenMental"
            rows="4"
            placeholder="Ingrese el examen mental..."
          />
        </div>

        <div class="form-group">
          <label>Nuevo análisis del caso</label>
          <textarea v-model="form.analisisCaso" rows="4" placeholder="Ingrese el análisis..." />
        </div>

        <div class="form-group">
          <label>Nuevo plan de manejo</label>
          <textarea v-model="form.planManejo" rows="4" placeholder="Ingrese el plan de manejo..." />
        </div>

        <div class="form-group">
          <label>Nuevo diagnóstico</label>
          <textarea v-model="form.diagnostico" rows="4" placeholder="Ingrese el diagnóstico..." />
        </div>

        <div class="form-group">
          <label>¿Requiere remisión?</label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" :value="true" v-model="form.requiereRemision" /> Sí
            </label>
            <label class="radio-label">
              <input type="radio" :value="false" v-model="form.requiereRemision" /> No
            </label>
          </div>
        </div>

        <div v-if="form.requiereRemision" class="form-group">
          <label>Especialidad a la que se remite <span class="required">*</span></label>
          <input
            type="text"
            v-model="form.especialidadRemision"
            placeholder="Ej. Psiquiatría, Neurología, Trabajo Social..."
            :required="form.requiereRemision"
          />
        </div>

        <div class="button-group">
          <button type="submit" class="btn-update" :disabled="!historiaClinica || cargando">
            {{ cargando ? 'Guardando...' : 'Guardar Cambios' }}
          </button>
          <button type="button" class="btn-clear" @click="cancelarEdicion" :disabled="cargando">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'

interface Cliente {
  id: number
  nombre: string
  identificacion: string
}

interface Psicologo {
  id: number
  nombre: string
}

interface HistoriaClinica {
  id: number
}

interface Consulta {
  id: number
  idHistoria: number
  idCliente: number
  fechaConsulta: string
  motivoConsulta: string
  examenMental: string
  analisisCaso: string
  planManejo: string
  diagnostico: string
  idPsicologo: number
  requiereRemision?: boolean // NUEVO: Mapeado opcional desde la respuesta JSON
  especialidadRemision?: string // NUEVO: Mapeado opcional desde la respuesta JSON
}

// Estados de navegación y listado general
const consultas = ref<Consulta[]>([])
const editando = ref(false)
const consultaIdActual = ref<number | null>(null)
const busquedaTabla = ref('')
const cargandoConsultas = ref(false)

// Datos auxiliares de llaves foráneas
const clientes = ref<Cliente[]>([])
const psicologos = ref<Psicologo[]>([])
const historiaClinica = ref<HistoriaClinica | null>(null)

const busquedaCliente = ref('')
const busquedaPsicologo = ref('')
const cargandoHistoria = ref(false)
const cargando = ref(false)

const form = ref({
  psicologoId: '',
  clienteId: '',
  fechaConsulta: '',
  motivoConsulta: '',
  examenMental: '',
  analisisCaso: '',
  planManejo: '',
  diagnostico: '',
  requiereRemision: false, // NUEVO: Inicializado por defecto
  especialidadRemision: '', // NUEVO: Inicializado por defecto
})

const API_URL_CLIENTES = 'http://localhost:8080/api/clientes'
const API_URL_USUARIOS = 'http://localhost:8080/usuario'
const API_URL_CONSULTAS = 'http://localhost:8080/consulta'

// Cargar todas las consultas para la tabla inicial
const cargarTodasConsultas = async () => {
  try {
    cargandoConsultas.value = true
    const response = await fetch(API_URL_CONSULTAS)
    if (response.ok) {
      consultas.value = await response.json()
    }
  } catch (error) {
    console.error('Error al cargar consultas:', error)
  } finally {
    cargandoConsultas.value = false
  }
}

const cargarClientes = async () => {
  try {
    const response = await fetch(API_URL_CLIENTES)
    if (response.ok) clientes.value = await response.json()
  } catch (error) {
    console.error('Error al cargar clientes:', error)
  }
}

const cargarPsicologos = async () => {
  try {
    const response = await fetch(API_URL_USUARIOS)
    if (response.ok) psicologos.value = await response.json()
  } catch (error) {
    console.error('Error al cargar psicólogos:', error)
  }
}

// Activa el modo edición e inyecta los datos de la fila seleccionada al formulario
const seleccionarConsulta = (consulta: Consulta) => {
  consultaIdActual.value = consulta.id

  // Rellenar campos clínicos de texto y fecha
  form.value.fechaConsulta = consulta.fechaConsulta ? consulta.fechaConsulta.substring(0, 16) : ''
  form.value.motivoConsulta = consulta.motivoConsulta || ''
  form.value.examenMental = consulta.examenMental || ''
  form.value.analisisCaso = consulta.analisisCaso || ''
  form.value.planManejo = consulta.planManejo || ''
  form.value.diagnostico = consulta.diagnostico || ''

  // NUEVO: Sincronizar datos de remisión guardados previamente
  form.value.requiereRemision = consulta.requiereRemision ?? false
  form.value.especialidadRemision = consulta.especialidadRemision || ''

  // Al usar IDs planos, el match con el valor numérico de las opciones de Vue es directo:
  if (consulta.idCliente) {
    form.value.clienteId = Number(consulta.idCliente) as any
  } else {
    form.value.clienteId = ''
  }

  if (consulta.idPsicologo) {
    form.value.psicologoId = Number(consulta.idPsicologo) as any
  } else {
    form.value.psicologoId = ''
  }

  editando.value = true
}

const cancelarEdicion = () => {
  editando.value = false
  consultaIdActual.value = null
  limpiarFormulario()
}

// Escucha cambios en el cliente para amarrar la historia clínica automáticamente
watch(
  () => form.value.clienteId,
  async (nuevoIdCliente) => {
    if (!nuevoIdCliente) {
      historiaClinica.value = null
      return
    }
    cargandoHistoria.value = true
    try {
      const response = await fetch(
        `http://localhost:8080/api/historias-clinicas/cliente/${nuevoIdCliente}`,
      )
      if (response.ok) {
        historiaClinica.value = await response.json()
      }
    } catch (error) {
      console.error('Error asignando historia clínica:', error)
    } finally {
      cargandoHistoria.value = false
    }
  },
)

// NUEVO: Resetear la especialidad si se cambia la remisión a "No" en edición
watch(
  () => form.value.requiereRemision,
  (nuevoValor) => {
    if (!nuevoValor) {
      form.value.especialidadRemision = ''
    }
  }
)

onMounted(() => {
  cargarTodasConsultas()
  cargarClientes()
  cargarPsicologos()
})

// Filtros reactivos y formateadores
const consultasFiltradasTabla = computed(() => {
  return consultas.value.filter(
    (c) =>
      c.id.toString().includes(busquedaTabla.value) ||
      c.motivoConsulta?.toLowerCase().includes(busquedaTabla.value.toLowerCase()) ||
      c.diagnostico?.toLowerCase().includes(busquedaTabla.value.toLowerCase()),
  )
})

const clientesFiltrados = computed(() =>
  clientes.value.filter((c) =>
    c.nombre?.toLowerCase().includes(busquedaCliente.value.toLowerCase()),
  ),
)

const psicologosFiltrados = computed(() =>
  psicologos.value.filter((p) =>
    p.nombre?.toLowerCase().includes(busquedaPsicologo.value.toLowerCase()),
  ),
)

const formatearFecha = (fechaStr: string) => {
  if (!fechaStr) return 'N/A'
  const fecha = new Date(fechaStr)
  return fecha.toLocaleString('es-CO', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// Guardar cambios mediante POST estructurado plano
const modificarConsulta = async () => {
  if (!historiaClinica.value) {
    alert('Se requiere una historia clínica vinculada para actualizar.')
    return
  }

  // Objeto coincide con ConsultaModel de Spring Boot (incluyendo los nuevos datos)
  const consultaModificadaPayload = {
    id: consultaIdActual.value,
    fechaConsulta: form.value.fechaConsulta,
    motivoConsulta: form.value.motivoConsulta,
    examenMental: form.value.examenMental,
    analisisCaso: form.value.analisisCaso,
    planManejo: form.value.planManejo,
    diagnostico: form.value.diagnostico,

    // NUEVOS CAMPOS: Enviados estructuradamente al backend
    requiereRemision: form.value.requiereRemision,
    especialidadRemision: form.value.requiereRemision ? form.value.especialidadRemision : null,

    // IDs planos mapeados uno a uno con las columnas de tu BD
    idCliente: Number(form.value.clienteId),
    idHistoria: Number(historiaClinica.value!.id),
    idPsicologo: Number(form.value.psicologoId),
  }

  try {
    cargando.value = true
    const response = await fetch('http://localhost:8080/consulta/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(consultaModificadaPayload),
    })

    if (response.ok) {
      alert('¡Consulta modificada con éxito en la base de datos!')
      await cargarTodasConsultas() // Refresca la tabla principal con el cambio real
      editando.value = false // Regresa automáticamente a la vista del listado
    } else {
      alert('Error al intentar guardar los cambios en el servidor.')
    }
  } catch (error) {
    console.error('Error de red al actualizar:', error)
  } finally {
    cargando.value = false
  }
}

const limpiarFormulario = () => {
  form.value = {
    psicologoId: '',
    clienteId: '',
    fechaConsulta: '',
    motivoConsulta: '',
    examenMental: '',
    analisisCaso: '',
    planManejo: '',
    diagnostico: '',
    requiereRemision: false, // Reset del nuevo campo
    especialidadRemision: '', // Reset del nuevo campo
  }
  busquedaCliente.value = ''
  busquedaPsicologo.value = ''
  historiaClinica.value = null
}
</script>

<style scoped>
/* Contenedores base */
.consultation-container {
  display: flex;
  justify-content: center;
  padding: 2rem;
  width: 100%;
  box-sizing: border-box;
}
.consultation-card {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
  transition: all 0.3s ease;
}
.wide-card {
  max-width: 1000px;
} 

.consultation-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}
.consultation-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
}
.title-container {
  flex: 1;
}
h2 {
  margin: 0;
  color: #0f6e56;
  font-size: 1.8rem;
}
.subtitle {
  margin-top: 0.3rem;
  color: #5f6b7a;
  font-size: 0.95rem;
}

/* Botones específicos */
.btn-back {
  background: #f0fdf9;
  color: #0f6e56;
  border: 2px solid #9fe1cb;
  padding: 8px 14px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-back:hover {
  background: #9fe1cb;
}
.btn-table-edit {
  background: #1d9e75;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-table-edit:hover {
  background: #0f6e56;
  transform: translateY(-1px);
}

/* Estilos de la Tabla */
.table-responsive {
  width: 100%;
  overflow-x: auto;
  margin-top: 1rem;
  border-radius: 12px;
  border: 1px solid #e1f7f2;
}
.consultas-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.95rem;
}
.consultas-table th {
  background-color: #eefcf8;
  color: #0f6e56;
  padding: 14px;
  font-weight: 700;
  border-bottom: 2px solid #9fe1cb;
}
.consultas-table td {
  padding: 14px;
  border-bottom: 1px solid #e1f7f2;
  color: #2c3e50;
}
.consultas-table tr:hover {
  background-color: #f7fdfb;
}

/* Truncado de textos largos en celdas */
.truncate-text {
  max-width: 220px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Elementos de formulario comunes */
.form-group {
  margin-bottom: 1.3rem;
}
label {
  display: block;
  margin-bottom: 0.5rem;
  color: #0f6e56;
  font-weight: 600;
}
.required {
  color: #e74c3c;
}
.search-input,
select,
input[type='datetime-local'],
input[type='text'], /* NUEVO: Agregado soporte de diseño para inputs de texto normales */
textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 12px 16px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  background: #f8fdfb;
  font-size: 1rem;
  transition: 0.3s;
  font-family: inherit;
}
.search-input {
  margin-bottom: 0.5rem;
}
textarea {
  resize: vertical;
}
select:focus,
input:focus,
textarea:focus {
  outline: none;
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

/* NUEVOS ESTILOS: Estructura visual para botones de selección (Radio) */
.radio-group {
  display: flex;
  gap: 24px;
  margin-top: 0.6rem;
}
.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5568;
  font-weight: 500;
  cursor: pointer;
  user-select: none;
}
.radio-label input[type='radio'] {
  accent-color: #1d9e75;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.button-group {
  display: flex;
  gap: 15px;
  margin-top: 2rem;
}
button {
  padding: 14px;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-update {
  background: linear-gradient(90deg, #68dcd2, #47a595);
  color: white;
  flex: 2;
}
.btn-clear {
  background: #e0e0e0;
  color: #555;
  flex: 1;
}
button:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-2px);
}
button:disabled {
  background: #d5dbdb;
  color: #7f8c8d;
  cursor: not-allowed;
  transform: none;
}

/* Estados Informativos */
.loading-state,
.empty-state {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
  font-weight: 500;
}
.info-status {
  margin-top: 0.4rem;
  font-size: 0.88rem;
  font-weight: 500;
}
.info-status.loading {
  color: #f39c12;
}
.info-status.success {
  color: #27ae60;
}

@media (max-width: 768px) {
  .consultation-card {
    padding: 1.5rem;
  }
  .consultation-header {
    flex-direction: column;
    text-align: center;
  }
  .button-group {
    flex-direction: column;
  }
  .truncate-text {
    max-width: 120px;
  }
}
</style>