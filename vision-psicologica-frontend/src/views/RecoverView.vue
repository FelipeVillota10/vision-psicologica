<template>
  <div class="recover-container">
    <div class="recover-card">
      <h2>Recupera tu contraseña</h2>
      <p class="subtitle">Por favor digita el email o identificacion asociados a tu cuenta</p>

      <form @submit.prevent>
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="form.email" placeholder="correo@ejemplo.com" />
        </div>

        <div class="form-group">
          <label for="identificacion">Identificación:</label>
          <input
            type="text"
            id="identificacion"
            v-model="form.identificacion"
            placeholder="Número de documento"
          />
        </div>

        <div class="button-group">
          <button type="button" class="btn-send" @click="enviarRecuperacion">Enviar</button>
          <button type="button" class="btn-back" @click="volver">Volver</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const form = ref({
  email: '',
  identificacion: '',
})

const enviarRecuperacion = () => {
  if (!form.value.email && !form.value.identificacion) {
    alert('Por favor, digita el email o la identificación.')
    return
  }

  alert('Solicitud enviada.')
}

const volver = () => {
  window.history.back()
}
</script>

<style scoped>
.recover-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 1rem;
  background: #f5f7fa;
}

.recover-card {
  background: white;
  padding: 2.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
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

input {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.button-group {
  display: flex;
  justify-content: space-between;
  gap: 0.5rem;
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

.btn-send {
  background-color: #3498db;
  color: white;
}

.btn-send:hover {
  background-color: #2980b9;
}

.btn-back {
  background-color: #7f8c8d;
  color: white;
}

.btn-back:hover {
  background-color: #636e72;
}
</style>
