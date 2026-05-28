<template>
  <div class="lp-wrap">
    <div class="lp-card">
      <div class="lp-layout">

        <div class="lp-left">
          <img src="@/assets/mariposa.png" alt="Logo Visión Psicológica" class="lp-logo" />
          <h1 class="lp-title">Visión Psicológica</h1>
          <h2 class="lp-title ">TM</h2>
        </div>

        <div class="lp-right">
          <div class="lp-field">
            <label class="lp-label">Correo</label>
            <input v-model="email" class="lp-input" type="email" placeholder="ejemplo@correo.com" />
          </div>
          <div class="lp-field">
            <label class="lp-label">Contraseña</label>
            <input v-model="password" class="lp-input" type="password" placeholder="••••••••" />
          </div>
          <a class="lp-forgot" href="#">¿Olvidaste la contraseña?</a>
          
          <!-- Botones con separación -->
          <div class="lp-buttons">
            <button class="lp-btn lp-btn-sesion" @click="iniciarSesion">Iniciar Sesión</button>
            <button class="lp-btn lp-btn-registro" @click="registrar">Registrar</button>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const email = ref('')
const password = ref('')
const iniciarSesion = () => { /* lógica login */ }
const registrar = () => { /* lógica registro */ }
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
  padding: 10.0rem 5.8rem 6rem;
  width: 300%;
  max-width: 1000px;
  border: 0.5px solid #c8f0e6;
  box-shadow: 0 4px 32px rgba(29,158,117,0.10);
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
.lp-right { flex: 1; }
.lp-logo { width: 380px; height: 380px; margin-bottom: 0.6rem; display: block; }
.lp-title { font-size: 20px; font-weight: 500; color: #0F6E56; margin: 0; text-align: center; }
.lp-field {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 1rem;
}
.lp-label {
  font-size: 20px;
  font-weight: 400;
  color: #0F6E56;
  white-space: nowrap;
  width: 80px;
  text-align: right;
}
.lp-input {
  flex: 1;
  box-sizing: border-box;
  padding: 10px 14px;
  border: 1.5px solid #9FE1CB;
  border-radius: 10px;
  font-size: 14px;
  background: #f8fdfb;
  outline: none;
  margin-bottom: 0;
  transition: border-color 0.2s;
}
.lp-input:focus { border-color: #1D9E75; background: #fff; }
.lp-forgot { display: block; text-align: right; font-size: 15px; color: #534AB7; margin-top: -0.5rem; margin-bottom: 1.4rem; }

/* Botones */
.lp-buttons {
  display: flex;
  gap: 1.2rem;        /* Espacio entre los botones */
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
  background: linear-gradient(90deg, #55bd9c, #5DCAA5);
}

.lp-btn-registro {
  background: linear-gradient(90deg, #7F77DD, #AFA9EC);
}
</style>