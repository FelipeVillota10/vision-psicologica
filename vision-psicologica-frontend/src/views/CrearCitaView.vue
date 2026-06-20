<template>
  <div class="appointment-container">
    <div class="appointment-card">

      <!-- Encabezado con logo (mismo estilo que HistoriaView) -->
      <div class="appointment-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="appointment-logo" />
        <div class="title-container">
          <h2>Crea tu próxima cita</h2>
          <p class="subtitle">Completa la información para agendar una nueva cita</p>
        </div>
      </div>

      <form @submit.prevent="agendarCita">
        <div class="form-group">
          <label for="cliente">Cliente <span class="required">*</span></label>
          <select id="cliente" v-model="form.clienteId" required>
            <option value="" disabled>Seleccione un cliente</option>
            <option
              v-for="cliente in clientes"
              :key="cliente.id"
              :value="cliente.id"
            >
              {{ cliente.nombre }} ({{ cliente.identificacion }})
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="psicologo">Psicólogo <span class="required">*</span></label>
          <select id="psicologo" v-model="form.psicologoId" required>
            <option value="" disabled>Seleccione un psicólogo</option>
            <option
              v-for="psicologo in psicologos"
              :key="psicologo.id"
              :value="psicologo.id"
            >
              {{ psicologo.nombre }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="fechaInicio">Fecha inicio <span class="required">*</span></label>
          <input
            type="datetime-local"
            id="fechaInicio"
            v-model="form.fechaInicio"
            required
          />
        </div>

        <div class="form-group">
          <label for="fechaFin">Fecha fin <span class="required">*</span></label>
          <input
            type="datetime-local"
            id="fechaFin"
            v-model="form.fechaFin"
            required
          />
        </div>

        <div class="button-group">
          <button
            type="submit"
            class="btn-schedule"
            :disabled="cargando"
          >
            {{ cargando ? 'Agendando...' : 'Agendar' }}
          </button>

          <button
            type="button"
            class="btn-clear"
            @click="limpiarFormulario"
            :disabled="cargando"
          >
            Limpiar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// Interfaces para tipar y evitar errores de tipo 'never'
interface Cliente {
  id: number;
  nombre: string;
  identificacion: string;
}

interface Psicologo {
  id: number;
  nombre: string;
}

// Estados reactivos cargados desde la base de datos
const clientes = ref<Cliente[]>([])
const psicologos = ref<Psicologo[]>([])
const cargando = ref(false)

// Estructura del formulario plano en el frontend
const form = ref({
  clienteId: '',
  psicologoId: '',
  fechaInicio: '',
  fechaFin: '',
})

// URLs base de tu backend de Spring Boot
const API_URL_USUARIOS = 'http://localhost:8080/usuario'
const API_URL_CLIENTES = 'http://localhost:8080/api/clientes' // Ajusta a /cliente si es en singular en tu sistema
const API_URL_CITAS = 'http://localhost:8080/api/citas'

// Cargar los datos reales al montar el componente
onMounted(async () => {
  try {
    // 1. Traer Clientes
    const resClientes = await fetch(API_URL_CLIENTES)
    if (resClientes.ok) {
      clientes.value = await resClientes.json()
    }

    // 2. Traer Psicólogos (Usuarios)
    const resPsicologos = await fetch(API_URL_USUARIOS)
    if (resPsicologos.ok) {
      psicologos.value = await resPsicologos.json()
    }
  } catch (error) {
    console.error('Error cargando datos iniciales de la BD:', error)
    alert('No se pudieron conectar los catálogos de la Base de Datos.')
  }
})

const agendarCita = async () => {
  if (
    !form.value.clienteId ||
    !form.value.psicologoId ||
    !form.value.fechaInicio ||
    !form.value.fechaFin
  ) {
    alert('Por favor completa todos los campos obligatorios.')
    return
  }

  // MAPEO CLAVE: Convertimos el formulario plano a la estructura de CitaModel esperada por Java
  const citaPayload = {
    cliente: { id: Number(form.value.clienteId) },
    psicologo: { id: Number(form.value.psicologoId) },
    fechaInicio: form.value.fechaInicio, // Envía el formato ISO compatible con LocalDateTime
    fechaFin: form.value.fechaFin
  }

  try {
    cargando.value = true

    const respuesta = await fetch(API_URL_CITAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(citaPayload)
    })

    const data = await respuesta.json()

    if (respuesta.ok) {
      alert('¡Cita agendada correctamente en el sistema!')
      limpiarFormulario()
    } else {
      // Captura los IllegalArgumentException controlados de tu backend (ej. choque de horarios)
      alert(`Error de validación: ${data.error || 'No se pudo agendar la cita'}`)
    }
  } catch (error) {
    console.error('Error de red al conectar con el servidor:', error)
    alert('Hubo un problema de conexión con el servidor de Spring Boot.')
  } finally {
    cargando.value = false
  }
}

const limpiarFormulario = () => {
  form.value.clienteId = ''
  form.value.psicologoId = ''
  form.value.fechaInicio = ''
  form.value.fechaFin = ''
}
</script>

<style scoped>
/* ── Contenedor principal ── */
.appointment-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

/* ── Card principal (mismo estilo que HistoriaView) ── */
.appointment-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3.5rem;
  width: 100%;
  max-width: 550px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

/* ── Encabezado con logo ── */
.appointment-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.appointment-logo {
  width: 100px;
  height: 100px;
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

/* ── Formulario ── */
.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  font-weight: 600;
  color: #0f6e56;
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.required {
  color: #e74c3c;
}

select,
input[type="datetime-local"] {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  font-size: 1rem;
  background: #f8fdfb;
  color: #1a202c;
  transition: all 0.3s;
  box-sizing: border-box;
  font-family: inherit;
}

select:focus,
input[type="datetime-local"]:focus {
  outline: none;
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

/* ── Botones (mismo gradiente que HistoriaView) ── */
.button-group {
  display: flex;
  gap: 14px;
  margin-top: 2rem;
}

button {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-schedule {
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: rgb(253, 253, 253);
}

.btn-clear {
  background: linear-gradient(90deg, #acadd6, #7e80da);
  color: rgb(255, 255, 255);
}

.btn-schedule:hover:not(:disabled),
.btn-clear:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-2px);
}

.btn-schedule:disabled,
.btn-clear:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* ── Responsivo ── */
@media (max-width: 600px) {
  .appointment-card {
    padding: 1.8rem 1.5rem;
  }
  .appointment-header {
    flex-direction: column;
    text-align: center;
    gap: 0.6rem;
  }
  .title-container {
    text-align: center;
  }
}
</style>