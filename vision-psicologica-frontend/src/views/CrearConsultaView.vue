<template>
  <div class="consultation-container">
    <div class="consultation-card">
      <div class="consultation-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="consultation-logo" />
        <div class="title-container">
          <h2>¿Has hecho una consulta? Regístrala</h2>
          <p class="subtitle">Completa la información clínica de la consulta realizada.</p>
        </div>
      </div>

      <form @submit.prevent="crearConsulta">
        <div class="form-group">
          <label>Psicólogo <span class="required">*</span></label>
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
          <label>Cliente <span class="required">*</span></label>
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
          <p v-else-if="form.clienteId && !historiaClinica" class="info-status error">
            ✗ Este cliente no tiene una historia clínica creada en el sistema.
          </p>
        </div>

        <div class="form-group">
          <label>Fecha y hora de la consulta <span class="required">*</span></label>
          <input type="datetime-local" v-model="form.fechaConsulta" required />
        </div>

        <div class="form-group">
          <label>Motivo de la consulta</label>
          <textarea
            v-model="form.motivoConsulta"
            rows="4"
            placeholder="Describa el motivo de la consulta..."
          />
        </div>

        <div class="form-group">
          <label>Examen mental</label>
          <textarea
            v-model="form.examenMental"
            rows="5"
            placeholder="Describa el examen mental..."
          />
        </div>

        <div class="form-group">
          <label>Análisis del caso</label>
          <textarea
            v-model="form.analisisCaso"
            rows="5"
            placeholder="Describa el análisis clínico..."
          />
        </div>

        <div class="form-group">
          <label>Plan de manejo</label>
          <textarea
            v-model="form.planManejo"
            rows="5"
            placeholder="Describa el plan de manejo..."
          />
        </div>

        <div class="form-group">
          <label>Diagnóstico</label>
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
          <button type="submit" class="btn-create" :disabled="!historiaClinica || cargando">
            {{ cargando ? 'Guardando...' : 'Crear' }}
          </button>
          <button type="button" class="btn-clear" @click="limpiarFormulario" :disabled="cargando">
            Limpiar
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
  requiereRemision: false, // Inicializado en false
  especialidadRemision: '', // Inicializado vacío
})

// URLs de tu Backend de Spring Boot
const API_URL_CLIENTES = 'http://localhost:8080/api/clientes'
const API_URL_USUARIOS = 'http://localhost:8080/usuario'

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

// Observador para traer la historia clínica al seleccionar el cliente
watch(
  () => form.value.clienteId,
  async (nuevoIdCliente) => {
    if (!nuevoIdCliente) {
      historiaClinica.value = null
      return
    }

    cargandoHistoria.value = true
    historiaClinica.value = null

    try {
      const response = await fetch(
        `http://localhost:8080/api/historias-clinicas/cliente/${nuevoIdCliente}`,
      )
      if (response.ok) {
        historiaClinica.value = await response.json()
      } else {
        console.warn('No se encontró historia clínica para este cliente.')
      }
    } catch (error) {
      console.error('Error al obtener la historia clínica:', error)
    } finally {
      cargandoHistoria.value = false
    }
  },
)

// Resetear la especialidad si el usuario cambia de "Sí" a "No" a mitad de camino
watch(
  () => form.value.requiereRemision,
  (nuevoValor) => {
    if (!nuevoValor) {
      form.value.especialidadRemision = ''
    }
  }
)

onMounted(() => {
  cargarClientes()
  cargarPsicologos()
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

const crearConsulta = async () => {
  if (!historiaClinica.value) {
    alert('No puedes registrar una consulta sin una historia clínica vinculada.')
    return
  }

  if (!form.value.psicologoId) {
    alert('Por favor, seleccione el psicólogo que atendió la consulta.')
    return
  }

  // Mapeo incluyendo las nuevas variables mapeadas con tu entidad de Spring Boot
  const consultaPayload = {
    fechaConsulta: form.value.fechaConsulta,
    motivoConsulta: form.value.motivoConsulta,
    examenMental: form.value.examenMental,
    analisisCaso: form.value.analisisCaso,
    planManejo: form.value.planManejo,
    diagnostico: form.value.diagnostico,
    
    // Nuevos campos
    requiereRemision: form.value.requiereRemision,
    especialidadRemision: form.value.requiereRemision ? form.value.especialidadRemision : null,

    // IDs planos directos
    idCliente: Number(form.value.clienteId),
    idHistoria: Number(historiaClinica.value!.id),
    idPsicologo: Number(form.value.psicologoId),
  }

  console.log('Payload enviado:', JSON.stringify(consultaPayload))

  try {
    cargando.value = true
    const response = await fetch('http://localhost:8080/consulta/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(consultaPayload),
    })

    if (response.ok) {
      alert('¡Consulta registrada con éxito!')
      limpiarFormulario()
    } else {
      alert('Hubo un problema en el servidor al guardar la consulta.')
    }
  } catch (error) {
    console.error('Error de red:', error)
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
    requiereRemision: false, // Limpieza del nuevo campo
    especialidadRemision: '', // Limpieza del nuevo campo
  }
  busquedaCliente.value = ''
  busquedaPsicologo.value = ''
  historiaClinica.value = null
}
</script>

<style scoped>
.consultation-container {
  display: flex;
  justify-content: center;
  padding: 2rem;
  width: 100%;
}
.consultation-card {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}
.consultation-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}
.consultation-logo {
  width: 100px;
  height: 100px;
  object-fit: contain;
}
.title-container {
  flex: 1;
}
h2 {
  margin: 0;
  color: #0f6e56;
  font-size: 1.9rem;
}
.subtitle {
  margin-top: 0.5rem;
  color: #5f6b7a;
}
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
input[type='text'], /* Añadido soporte para inputs de texto genéricos */
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
  min-height: 100px;
}
select:focus,
input:focus,
textarea:focus {
  outline: none;
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

/* NUEVOS ESTILOS: Diseñados para que combinen con la paleta actual */
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
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}
.btn-create {
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: white;
}
.btn-clear {
  background: linear-gradient(90deg, #acadd6, #7e80da);
  color: white;
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
  opacity: 1;
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
.info-status.error {
  color: #c0392b;
}

@media (max-width: 700px) {
  .consultation-card {
    padding: 1.5rem;
  }
  .consultation-header {
    flex-direction: column;
    text-align: center;
  }
  .title-container {
    text-align: center;
  }
  .button-group {
    flex-direction: column;
  }
}
</style>