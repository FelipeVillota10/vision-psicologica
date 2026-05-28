<template>
  <div class="login-container">
    <div class="login-card">
      <h2>Visión Psicológica</h2>
      <p class="subtitle">Control de Acceso</p>

      <form @submit.prevent="manejarLogin">
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            type="email"
            id="email"
            v-model="email"
            placeholder="ejemplo@correo.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Contraseña:</label>
          <input
            type="password"
            id="password"
            v-model="contrasena"
            placeholder="********"
            required
          />
        </div>

        <div class="actions-link">
          <RouterLink to="/recuperacion">¿Olvidaste tu contraseña?</RouterLink>
        </div>

        <div class="button-group">
          <button type="submit" class="btn-primary">Iniciar Sesión</button>

          <button type="button" class="btn-secondary" @click="router.push('/registro')">
            Registrar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const contrasena = ref('')

const manejarLogin = async () => {
  if (!email.value || !contrasena.value) return

  const exito = await authStore.iniciarSesion(email.value, contrasena.value)

  if (exito) {
    router.push('/dashboard')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
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

.actions-link {
  text-align: right;
  margin-bottom: 1.5rem;
}

.actions-link a {
  color: #3498db;
  text-decoration: none;
  font-size: 0.9rem;
}

.actions-link a:hover {
  text-decoration: underline;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

button {
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary {
  background-color: #2ecc71;
  color: white;
}

.btn-primary:hover {
  background-color: #27ae60;
}

.btn-secondary {
  background-color: #e74c3c;
  color: white;
}

.btn-secondary:hover {
  background-color: #c0392b;
}
</style>
