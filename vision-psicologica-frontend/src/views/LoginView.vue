<template>
  <div class="lp-wrap">
    <div class="lp-card">
      <div class="lp-layout">
        <div class="lp-left">
          <img src="@/assets/mariposa.png" alt="Logo Visión Psicológica" class="lp-logo" />
          <h1 class="lp-title">Visión Psicológica</h1>
          <h2 class="lp-title">TM</h2>
        </div>

        <div class="lp-right">
          <form @submit.prevent="manejarLogin">
            <div class="lp-field">
              <label class="lp-label" for="email">Correo</label>
              <input
                id="email"
                v-model="email"
                class="lp-input"
                type="email"
                placeholder="ejemplo@correo.com"
                required
              />
            </div>

            <div class="lp-field">
              <label class="lp-label" for="password">Contraseña</label>
              <input
                id="password"
                v-model="contrasena"
                class="lp-input"
                type="password"
                placeholder="••••••••"
                required
              />
            </div>

            <RouterLink class="lp-forgot" to="/recuperacion">
              ¿Olvidaste la contraseña?
            </RouterLink>

            <div class="lp-buttons">
              <button type="submit" class="lp-btn lp-btn-sesion" style="width: 100%;">
                Iniciar Sesión
              </button>
            </div>
          </form>
        </div>
      </div>
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
.lp-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e4bef3 0%, #baf5f2 100%);
  padding: 2rem;
  box-sizing: border-box;
}

.lp-card {
  background: #e7f3ef;
  border-radius: 20px;
  padding: 10rem 5.8rem 6rem;
  width: 300%;
  max-width: 1000px;
  border: 0.5px solid #c8f0e6;
  box-shadow: 0 4px 32px rgba(29, 158, 117, 0.1);
}

.lp-layout {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.lp-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 130px;
}

.lp-right {
  flex: 1;
}

.lp-logo {
  width: 380px;
  height: 380px;
  margin-bottom: 0.6rem;
  display: block;
}

.lp-title {
  font-size: 20px;
  font-weight: 500;
  color: #0f6e56;
  margin: 0;
  text-align: center;
}

.lp-field {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 1rem;
}

.lp-label {
  font-size: 20px;
  font-weight: 400;
  color: #0f6e56;
  white-space: nowrap;
  width: 80px;
  text-align: right;
}

.lp-input {
  flex: 1;
  box-sizing: border-box;
  padding: 10px 14px;
  border: 1.5px solid #9fe1cb;
  border-radius: 10px;
  font-size: 14px;
  background: #f8fdfb;
  outline: none;
  margin-bottom: 0;
  transition: border-color 0.2s;
}

.lp-input:focus {
  border-color: #1d9e75;
  background: #fff;
}

.lp-forgot {
  display: block;
  text-align: right;
  font-size: 15px;
  color: #534ab7;
  margin-top: -0.5rem;
  margin-bottom: 1.4rem;
  text-decoration: none;
}

.lp-forgot:hover {
  text-decoration: underline;
}

.lp-buttons {
  display: flex;
  gap: 1.2rem;
  margin-top: 1.2rem;
}

.lp-btn {
  width: 50%;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  color: #fff;
  transition: opacity 0.15s;
}

.lp-btn:hover {
  opacity: 0.9;
}

.lp-btn-sesion {
  background: linear-gradient(90deg, #55bd9c, #5dcaa5);
}

.lp-btn-registro {
  background: linear-gradient(90deg, #7f77dd, #afa9ec);
}
</style>