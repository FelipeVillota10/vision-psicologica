<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="register-logo" />
        
        <div class="title-container">
          <h2>Crea un usuario</h2>
          <p class="subtitle">Ingresa los datos del nuevo usuario</p>
        </div>
      </div>

      <form @submit.prevent>
        <div class="form-group">
          <label for="nombre">Nombre <span class="required">*</span></label>
          <input type="text" id="nombre" v-model="form.nombre" placeholder="Nombre completo" />
        </div>

        <div class="form-group">
          <label for="identificacion">Identificación <span class="required">*</span></label>
          <input
            type="text"
            id="identificacion"
            v-model="form.identificacion"
            placeholder="Número de documento"
          />
        </div>

        <div class="form-group">
          <label for="email">Email <span class="required">*</span></label>
          <input type="email" id="email" v-model="form.email" placeholder="correo@ejemplo.com" />
        </div>

        <div class="form-group">
          <label for="contrasena">Contraseña <span class="required">*</span></label>
          <input
            type="password"
            id="contrasena"
            v-model="form.contrasena"
            placeholder="Contraseña de usuario"
          />
        </div>

        <div class="button-group">
          <button type="button" class="btn-create" @click="manejarRegistro">Crear</button>
          <button type="button" class="btn-clear" @click="limpiarFormulario">Limpiar</button>
          <button type="button" class="btn-delete" @click="borrarFormulario">Borrar</button>
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
  if (!form.value.nombre || !form.value.identificacion || !form.value.email || !form.value.contrasena) {
    alert('Por favor, completa todos los campos obligatorios.')
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
    alert('Usuario creado exitosamente')
  }
}

const limpiarFormulario = () => {
  form.value.nombre = ''
  form.value.identificacion = ''
  form.value.email = ''
  form.value.contrasena = ''
}

const borrarFormulario = () => {
  if (confirm('¿Estás seguro de borrar todos los datos?')) {
    limpiarFormulario()
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0f7f4 0%, #ede8fb 100%);
  padding: 2rem;
  box-sizing: border-box;
}

.register-card {
  background: #fff;
  border-radius: 20px;
  padding: 3rem 4.5rem;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

.register-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2.5rem;
}

.register-logo {
  width: 140px;
  height: 140px;
  flex-shrink: 0;
}

.title-container {
  flex: 1;
  text-align: center;
}

h2 {
  color: #0F6E56;
  margin: 0 0 0.4rem 0;
  font-size: 2.0rem;
}

.subtitle {
  color: #5f6b7a;
  margin: 0;
  font-size: 1.15rem;
}

/* Resto de estilos (form-group, inputs, botones) se mantienen iguales */
.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  font-weight: 600;
  color: #0F6E56;
  margin-bottom: 0.65rem;
  font-size: 1.05rem;
}

.required {
  color: #e74c3c;
}

input {
  width: 100%;
  padding: 14px 20px;
  border: 2px solid #9FE1CB;
  border-radius: 12px;
  font-size: 1.02rem;
  background: #f8fdfb;
  transition: all 0.3s;
}

input:focus {
  outline: none;
  border-color: #1D9E75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

.button-group {
  display: flex;
  gap: 14px;
  margin-top: 2.2rem;
}

button {
  flex: 1;
  padding: 14px 20px;
  border: none;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-create { background: linear-gradient(90deg, #baf5f2, #47a595); color: rgb(253, 253, 253); }
.btn-clear  { background: linear-gradient(90deg, #478d89, #7e80da); color: rgb(255, 255, 255); }
.btn-delete { background: linear-gradient(90deg, #acadd6, #7e80da); color: rgb(255, 255, 255); }

.btn-create:hover, .btn-clear:hover, .btn-delete:hover {
  opacity: 0.92;
  transform: translateY(-2px);
}
</style>