<template>
  <div class="perfil-container">
    <div class="perfil-card">
      <div class="perfil-header">
        <div class="title-container">
          <h2>Bienvenido a tu perfil</h2>
          <p class="subtitle">Gestiona y actualiza tus datos personales</p>
        </div>

        <button
          type="button"
          class="edit-button"
          @click="toggleEdicion"
          :title="modoEdicion ? 'Cancelar edición' : 'Editar información'"
        >
          <svg
            v-if="!modoEdicion"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            class="edit-icon"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M12 20h9" />
            <path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z" />
          </svg>
          <span v-else class="close-icon">✕</span>
        </button>
      </div>

      <div v-if="mensajeExito" class="alert alert-success">{{ mensajeExito }}</div>
      <div v-if="mensajeError" class="alert alert-danger">{{ mensajeError }}</div>

      <form @submit.prevent="guardarCambios" class="perfil-body">
        <div class="form-group">
          <label for="nombre">Nombre:</label>
          <input
            id="nombre"
            v-model="formulario.nombre"
            type="text"
            :readonly="!modoEdicion"
            required
          />
        </div>

        <div class="form-group">
          <label for="identificacion">Identificación:</label>
          <input
            id="identificacion"
            v-model="formulario.identificacion"
            type="text"
            :readonly="!modoEdicion"
            required
          />
        </div>

        <div class="form-group">
          <label for="email">Email:</label>
          <input
            id="email"
            v-model="formulario.email"
            type="email"
            :readonly="!modoEdicion"
            required
          />
        </div>

        <div class="form-group">
          <label for="contrasena">Contraseña:</label>
          <input
            id="contrasena"
            v-model="formulario.contrasena"
            type="password"
            :readonly="!modoEdicion"
            placeholder="••••••••"
          />
        </div>

        <div class="note">
          <span v-if="modoEdicion" class="text-edition">Edición activada. Modifica tus campos y presiona guardar.</span>
          <span v-else>Modo visualización.</span>
        </div>

        <button v-if="modoEdicion" type="submit" class="btn-guardar" :disabled="cargando">
          {{ cargando ? 'Guardando...' : 'Guardar Cambios' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const modoEdicion = ref(false);
const cargando = ref(false);
const mensajeExito = ref('');
const mensajeError = ref('');

const formulario = ref({
  id: null,
  nombre: '',
  identificacion: '',
  email: '',
  contrasena: '' // Se añade la propiedad al estado reactivo
});

// Respaldo para restaurar datos si el usuario cancela la edición
let copiaRespaldo = { ...formulario.value };

const obtenerIdUsuarioLogueado = (): string | null => {
  return localStorage.getItem('idUsuario');
};

// 1. Cargar los datos del usuario desde el Backend
const cargarPerfil = async () => {
  const idUsuario = obtenerIdUsuarioLogueado();
  if (!idUsuario) {
    mensajeError.value = 'No se encontró una sesión activa de usuario.';
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/usuario/${idUsuario}`);
    if (response.ok) {
      const data = await response.json();
      formulario.value = {
        id: data.id,
        nombre: data.nombre,
        identificacion: data.identificacion,
        email: data.email,
        contrasena: '' // Llega nula del backend por seguridad, la manejamos vacía localmente
      };
      copiaRespaldo = { ...formulario.value };
    } else {
      mensajeError.value = 'Error al obtener los datos del perfil.';
    }
  } catch (error) {
    console.error('Error de red:', error);
    mensajeError.value = 'No se pudo conectar con el servidor.';
  }
};

// 2. Enviar los datos actualizados mediante PUT
const guardarCambios = async () => {
  cargando.value = true;
  mensajeExito.value = '';
  mensajeError.value = '';

  // Clonamos los datos para no alterar directamente el formulario visual
  const datosAEnviar = { ...formulario.value };

  // 💡 Alerta de seguridad: Si el usuario no escribió nada en la contraseña, 
  // eliminamos la propiedad para evitar sobreescribir la contraseña real con un valor vacío en la BD.
  if (!datosAEnviar.contrasena) {
    delete datosAEnviar.contrasena;
  }

  try {
    const response = await fetch(`http://localhost:8080/usuario/${formulario.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(datosAEnviar)
    });

    if (response.ok) {
      mensajeExito.value = '¡Perfil actualizado con éxito!';
      modoEdicion.value = false;
      formulario.value.contrasena = ''; // Limpiamos el texto plano de la contraseña editada
      copiaRespaldo = { ...formulario.value };
    } else {
      mensajeError.value = 'Ocurrió un error al intentar actualizar el perfil.';
    }
  } catch (error) {
    console.error('Error al actualizar:', error);
    mensajeError.value = 'Error de red. No se pudieron guardar los cambios.';
  } finally {
    cargando.value = false;
  }
};

const toggleEdicion = () => {
  if (modoEdicion.value) {
    formulario.value = { ...copiaRespaldo };
  }
  modoEdicion.value = !modoEdicion.value;
  mensajeExito.value = '';
  mensajeError.value = '';
};

onMounted(() => {
  cargarPerfil();
});
</script>

<style scoped>
.perfil-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  width: 100%;
  min-height: 100%;
  box-sizing: border-box;
}

.perfil-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 560px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

.perfil-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 2rem;
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

.edit-button {
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 18px rgba(29, 158, 117, 0.18);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.edit-button:hover {
  transform: translateY(-2px);
  opacity: 0.95;
}

.edit-icon {
  width: 22px;
  height: 22px;
}

.close-icon {
  font-size: 1.3rem;
  font-weight: bold;
}

.perfil-body {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: left;
}

label {
  font-weight: 600;
  color: #0f6e56;
  font-size: 1rem;
}

input {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  font-size: 1rem;
  background: #f8fdfb;
  transition: all 0.3s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #1d9e75;
  background: white;
  box-shadow: 0 0 0 4px rgba(29, 158, 117, 0.12);
}

input[readonly] {
  background: #f3f7f6;
  cursor: default;
  color: #4d5b66;
  border-color: #e2ece9;
}

.note {
  margin-top: 0.5rem;
  font-size: 0.95rem;
  color: #5f6b7a;
  text-align: left;
}

.text-edition {
  color: #c97d16;
  font-weight: 500;
}

.alert {
  padding: 10px 15px;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
  text-align: left;
}
.alert-success {
  background-color: #e6f6f0;
  color: #155724;
  border: 1px solid #c3e6cb;
}
.alert-danger {
  background-color: #fce8e6;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.btn-guardar {
  background-color: #1d9e75;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 0.5rem;
}
.btn-guardar:hover {
  background-color: #0f6e56;
}
.btn-guardar:disabled {
  background-color: #a3dac8;
  cursor: not-allowed;
}
</style>