<template>
  <div class="cliente-container">
    <div class="cliente-card">
      <div class="cliente-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="cliente-logo" />
        <div class="title-container">
          <h2>Crear cliente</h2>
          <p class="subtitle">Ingresa los datos del nuevo cliente</p>
        </div>
      </div>

      <form @submit.prevent="guardarCliente">
        
        <div class="form-group">
          <label for="nombre">Nombre <span class="required">*</span></label>
          <input
            type="text"
            id="nombre"
            v-model="form.nombre"
            placeholder="Ej. Juan Pérez"
            required
          />
        </div>

        <div class="form-group">
          <label for="identificacion">Identificación <span class="required">*</span></label>
          <input
            type="text"
            id="identificacion"
            v-model="form.identificacion"
            placeholder="Número de documento"
            required
          />
        </div>

        <div class="form-group">
          <label for="telefono">Teléfono <span class="required">*</span></label>
          <input
            type="text"
            id="telefono"
            v-model="form.telefono"
            placeholder="Ej. 3001234567"
            required
          />
        </div>

        <div class="form-group">
          <label for="correoElectronico">Correo electrónico <span class="required">*</span></label>
          <input
            type="email"
            id="correoElectronico"
            v-model="form.correoElectronico"
            placeholder="correo@ejemplo.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="direccion">Dirección <span class="required">*</span></label>
          <input
            type="text"
            id="direccion"
            v-model="form.direccion"
            placeholder="Ej. Calle 123 # 45-67"
            required
          />
        </div>

        <div class="button-group">
          <button type="submit" class="btn-create" :disabled="cargando">
            {{ cargando ? 'Guardando...' : 'Guardar cliente' }}
          </button>
          
          <button type="button" class="btn-clear" @click="cancelar">Cancelar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const form = ref({
  nombre: '',
  identificacion: '',
  telefono: '',
  correoElectronico: '',
  direccion: '',
})

// Variable pequeña para deshabilitar el botón mientras guarda
const cargando = ref(false)

const guardarCliente = async () => {
  // Punto 3: Validar que todos los campos estén llenos
  if (
    !form.value.nombre ||
    !form.value.identificacion ||
    !form.value.telefono ||
    !form.value.correoElectronico ||
    !form.value.direccion
  ) {
    alert('Por favor, completa todos los campos obligatorios.')
    return
  }

  // Punto 4: El campo nombre solo podrá contener caracteres alfabéticos
  const regexNombre = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/
  if (!regexNombre.test(form.value.nombre)) {
    alert('Error: El nombre solo puede contener letras y espacios.')
    return
  }

  // Punto 5: El campo identificación solo podrá contener caracteres numéricos
  const regexIdentificacion = /^[0-9]+$/
  if (!regexIdentificacion.test(form.value.identificacion)) {
    alert('Error: La identificación solo debe contener números.')
    return
  }

  try {
    cargando.value = true // Cambiamos el texto del botón
    
    // Petición real al backend Spring Boot
    const response = await fetch('http://localhost:8080/api/clientes', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        nombre: form.value.nombre,
        identificacion: form.value.identificacion,
        telefono: form.value.telefono,
        correoElectronico: form.value.correoElectronico,
        direccion: form.value.direccion,
        usuario: { id: 8 } // <-- IMPORTANTE: ID del psicólogo para la relación en DB
      })
    });

    if (response.ok) {
      alert('Cliente guardado en el sistema exitosamente');
      limpiarFormulario();
    } else {
      const error = await response.text();
      alert('Error al guardar: ' + error);
    }

  } catch (error) {
    console.error("Hubo un problema con la petición:", error)
    alert('Ocurrió un error al intentar conectar con el servidor.');
  } finally {
    cargando.value = false // Restauramos el botón
  }
}

// Punto 7: Acción del botón cancelar
const cancelar = () => {
  limpiarFormulario()
}

const limpiarFormulario = () => {
  form.value.nombre = ''
  form.value.identificacion = ''
  form.value.telefono = ''
  form.value.correoElectronico = ''
  form.value.direccion = ''
}
</script>

<style scoped>
/* Contenedor adaptado para vivir dentro del Dashboard */
.cliente-container {
  display: flex;
  align-items: flex-start; /* Para que la tarjeta no quede forzada al centro vertical si el dashboard crece */
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}

.cliente-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3.5rem;
  width: 100%;
  max-width: 550px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

.cliente-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.cliente-logo {
  width: 100px; /* Un poco más pequeño que el login para que no ocupe tanto espacio en el dashboard */
  height: 100px;
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

.btn-create {
  background: linear-gradient(90deg, #baf5f2, #47a595);
  color: rgb(253, 253, 253);
}

.btn-clear {
  background: linear-gradient(90deg, #acadd6, #7e80da);
  color: rgb(255, 255, 255);
}

.btn-create:hover:not(:disabled),
.btn-clear:hover {
  opacity: 0.92;
  transform: translateY(-2px);
}

.btn-create:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>