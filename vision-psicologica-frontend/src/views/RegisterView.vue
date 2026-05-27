<template>
  <div class="register-container">
    <div class="register-card">
      <h2>Crea un usuario</h2>
      <p class="subtitle">Ingresa los datos del nuevo usuario</p>

      <form @submit.prevent>
        <div class="form-group">
          <label for="nombre">Nombre:</label>
          <input type="text" id="nombre" v-model="form.nombre" placeholder="Nombre completo" />
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

        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" v-model="form.email" placeholder="correo@ejemplo.com" />
        </div>

        <div class="form-group">
          <label for="contrasena">Contraseña:</label>
          <input
            type="text"
            id="contrasena"
            v-model="form.contrasena"
            placeholder="Contraseña de usuario"
          />
        </div>

        <div class="button-group">
          <button type="button" class="btn-create" @click="manejarRegistro">Crear</button>
          <button type="button" class="btn-clear" @click="limpiarFormulario">Limpiar</button>
          <button type="button" class="btn-delete">Borrar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const form = ref({
  nombre: '',
  identificacion: '',
  email: '',
  contrasena: '',
})

const manejarRegistro = async () => {
  if (!form.value.nombre || !form.value.identificacion || !form.value.email) {
    alert('Por favor, llena los campos obligatorios.')
    return
  }

  const exito = await authStore.registrarUsuario({
    nombre: form.value.nombre,
    identificacion: form.value.identificacion,
    email: form.value.email,
    contrasena: form.value.contrasena,
  })
  if (exito) {
    limpiarFormulario()
  }
}

const limpiarFormulario = () => {
  form.value.nombre = ''
  form.value.identificacion = ''
  form.value.email = ''
  form.value.contrasena = ''
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.register-card {
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

.btn-create {
  background-color: #3498db;
  color: white;
}

.btn-create:hover {
  background-color: #2980b9;
}

.btn-clear {
  background-color: #f1c40f;
  color: white;
}

.btn-clear:hover {
  background-color: #f39c12;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background-color: #c0392b;
}
</style>
