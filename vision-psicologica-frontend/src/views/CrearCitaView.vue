<template>
  <div class="appointment-container">
    <div class="appointment-card">
      <h2>Crea tu próxima cita</h2>
      <p class="subtitle">Completa la información para agendar una nueva cita</p>

      <form @submit.prevent="agendarCita">
        <div class="form-group">
          <label for="cliente">Cliente:</label>
          <select id="cliente" v-model="form.clienteId" required>
            <option value="">Seleccione un cliente</option>
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
          <label for="psicologo">Psicólogo:</label>
          <select id="psicologo" v-model="form.psicologoId" required>
            <option value="">Seleccione un psicólogo</option>
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
          <label for="fechaInicio">Fecha inicio:</label>
          <input
            type="datetime-local"
            id="fechaInicio"
            v-model="form.fechaInicio"
            required
          />
        </div>

        <div class="form-group">
          <label for="fechaFin">Fecha fin:</label>
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
          >
            Agendar
          </button>

          <button
            type="button"
            class="btn-clear"
            @click="limpiarFormulario"
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
.appointment-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.appointment-card {
  background: white;
  padding: 2.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
  text-align: center;
}

h2 {
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.subtitle {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  text-align: left;
  margin-bottom: 1.2rem;
}

label {
  font-weight: 600;
  margin-bottom: 0.5rem;
}

input,
select {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.button-group {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-schedule {
  background-color: #3498db;
  color: white;
}

.btn-schedule:hover {
  background-color: #2980b9;
}

.btn-clear {
  background-color: #f1c40f;
  color: white;
}

.btn-clear:hover {
  background-color: #f39c12;
}
</style>