<template>
  <div class="recover-container">
    <div class="recover-card">
      <div class="recover-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="register-logo" />
        <h2>Recupera tu contraseña</h2>
      </div>

      <form v-if="fase === 'solicitud'" @submit.prevent="procesarSolicitud">
        <p class="subtitle">Por favor digita el email o identificación asociados a tu cuenta para generar una clave temporal</p>
        
        <div class="form-group">
          <label for="email">Email de Usuario:</label>
          <input 
            type="email" 
            id="email" 
            v-model="formSolicitud.email" 
            placeholder="correo@ejemplo.com" 
          />
        </div>

        <div class="form-group">
          <label for="identificacion">Identificación:</label>
          <input
            type="text"
            id="identificacion"
            v-model="formSolicitud.identificacion"
            placeholder="Número de documento"
          />
        </div>

        <div class="button-group">
          <button type="submit" class="btn-send" :disabled="cargando">
            {{ cargando ? 'Buscando...' : 'Enviar Clave Temporal' }}
          </button>
          <button type="button" class="btn-back" @click="volver" :disabled="cargando">Volver</button>
        </div>
      </form>

      <form v-else-if="fase === 'cambio'" @submit.prevent="procesarCambioContrasena">
        <p class="subtitle alert-subtitle">🔑 ¡Usuario localizado! Por seguridad, debes establecer una nueva contraseña para actualizar la base de datos.</p>

        <div class="form-group">
          <label for="claveTemporal">Contraseña Temporal Recibida:</label>
          <input 
            type="password" 
            id="claveTemporal" 
            v-model="formCambio.temporal" 
            placeholder="Ingrese la contraseña temporal"
            required
          />
        </div>

        <div class="form-group">
          <label for="nuevaContrasena">Nueva Contraseña:</label>
          <input 
            type="password" 
            id="nuevaContrasena" 
            v-model="formCambio.nueva" 
            placeholder="Mínimo 4 caracteres"
            required
          />
        </div>

        <div class="form-group">
          <label for="confirmarContrasena">Confirmar Nueva Contraseña:</label>
          <input 
            type="password" 
            id="confirmarContrasena" 
            v-model="formCambio.confirmar" 
            placeholder="Repita la nueva contraseña"
            required
          />
        </div>

        <div class="button-group">
          <button type="submit" class="btn-send" :disabled="cargando">
            {{ cargando ? 'Guardando...' : 'Guardar Contraseña' }}
          </button>
          <button type="button" class="btn-back" @click="fase = 'solicitud'" :disabled="cargando">Cancelar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const fase = ref<'solicitud' | 'cambio'>('solicitud')
const cargando = ref(false)

const idUsuarioCapturado = ref<number | null>(null)
const claveTemporalCorrecta = ref('')

const formSolicitud = ref({
  email: '',
  identificacion: ''
})

const formCambio = ref({
  temporal: '',
  nueva: '',
  confirmar: ''
})

const procesarSolicitud = async () => {
  if (!formSolicitud.value.email.trim() && !formSolicitud.value.identificacion.trim()) {
    alert('Error: Por favor introduce el correo o el número de identificación.')
    return
  }

  cargando.value = true
  try {
    const response = await fetch('http://localhost:8080/usuario/recuperar-solicitud', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: formSolicitud.value.email,
        identificacion: formSolicitud.value.identificacion
      })
    })

    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.error || 'Error en la solicitud')
    }

    alert(`Sistema: ${data.message}\n\n(Para la sustentación usa la clave temporal enviada: ${data.claveTemporal})`)

    idUsuarioCapturado.value = data.idUsuario
    claveTemporalCorrecta.value = data.claveTemporal

    fase.value = 'cambio'
  } catch (error: unknown) {
  if (error instanceof Error) {
    alert(error.message)
  } else {
    alert('Error inesperado')
  }
} finally {
    cargando.value = false
  }
}

// Lógica de Fase 2: Validar e impactar en base de datos real (Criterios 4 y 5)
const procesarCambioContrasena = async () => {
  if (formCambio.value.temporal !== claveTemporalCorrecta.value) {
    alert('Error: La contraseña temporal ingresada es incorrecta.')
    return
  }

  if (formCambio.value.nueva !== formCambio.value.confirmar) {
    alert('Error: La nueva contraseña y la confirmación no coinciden (Criterio 4).')
    return
  }

  if (formCambio.value.nueva.length < 4) {
    alert('Error: La contraseña debe tener al menos 4 caracteres.')
    return
  }

  cargando.value = true
  try {
    // Criterio 5: Oprimir botón de guardar contraseña para impactar la Base de Datos
    const response = await fetch('http://localhost:8080/usuario/guardar-nueva-contrasena', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        idUsuario: idUsuarioCapturado.value,
        nuevaContrasena: formCambio.value.nueva
      })
    })

    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.error || 'Error al actualizar')
    }

    alert(data.message)
    fase.value = 'solicitud'
    router.push('/') 
  } catch (error: unknown) {
  if (error instanceof Error) {
    alert(error.message)
  } else {
    alert('Error inesperado')
  }
} finally {
    cargando.value = false
  }
}

const volver = () => {
  router.push('/')
}
</script>

<style scoped>
.recover-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 1rem;
  background: #ecf5f5;
}

.recover-card {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
  width: 100%;
  max-width: 480px;
}

.recover-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.register-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

h2 {
  margin: 0;
  color: #344a73;
  font-size: 1.8rem;
  text-align: center;
}

.subtitle {
  color: #5f6b7a;
  margin: 0 0 1.5rem 0;
  font-size: 0.95rem;
  text-align: center;
  line-height: 1.4;
}

.alert-subtitle {
  color: #2c8a6e;
  background-color: #e0f5f5;
  padding: 10px;
  border-radius: 8px;
  font-weight: 500;
}

.form-group {
  display: flex;
  flex-direction: column;
  text-align: left;
  margin-bottom: 1.2rem;
  gap: 0.4rem;
}

label {
  font-weight: 600;
  color: #344a73;
  font-size: 0.95rem;
}

input {
  padding: 12px 14px;
  border: 1.5px solid #b8b9e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background: #fdfdff;
  transition: border-color 0.2s;
}

input:focus {
  border-color: #344a73;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

button {
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-send {
  background-color: #b8b9e0;
  color: #1a1a1a;
}

.btn-send:hover:not(:disabled) {
  background-color: #a8a9d8;
}

.btn-back {
  background-color: #e2e4f0;
  color: #344a73;
}

.btn-back:hover:not(:disabled) {
  background-color: #c5c6eb;
}
</style>