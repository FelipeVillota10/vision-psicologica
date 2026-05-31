<template>
  <div class="historia-container">
    <div class="historia-card">
      <div class="historia-header">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="historia-logo" />
        <div class="title-container">
          <h2>Crear historia Clínica</h2>
          <p class="subtitle">Selecciona el cliente y la fecha de creación</p>
        </div>
      </div>

      <form @submit.prevent="manejarEnvio">
        <div class="form-group">
          <label for="cliente">Cliente <span class="required">*</span></label>
          <select id="cliente" v-model="formulario.clienteId" required>
            <option value="" disabled>Seleccionar</option>
            <option
              v-for="cliente in listaClientes"
              :key="cliente.id"
              :value="cliente.id"
            >
              {{ cliente.nombre }} ({{ cliente.identificacion }})
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="fecha">Fecha de creación <span class="required">*</span></label>
          <input
            type="date"
            id="fecha"
            v-model="formulario.fechaCreacion"
            required
          />
        </div>

        <div class="button-group">
          <button type="submit" class="btn-create" :disabled="cargando">
            {{ cargando ? 'Creando...' : 'Crear' }}
          </button>

          <button type="button" class="btn-clear" @click="limpiarFormulario" :disabled="cargando">
            Limpiar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

interface Cliente {
  id: number;
  nombre: string;
  identificacion: string;
  telefono?: string;
  correoElectronico?: string;
  direccion?: string;
}

const formularioInicial = {
  clienteId: '',
  fechaCreacion: ''
};

const formulario = ref({ ...formularioInicial });
const listaClientes = ref<Cliente[]>([]);
const cargando = ref(false);

const API_URL = 'http://localhost:8080/api/clientes';

const cargarClientesDesdeBD = async () => {
  try {
    const respuesta = await fetch(API_URL);
    if (!respuesta.ok) {
      throw new Error('No se pudo obtener la lista de clientes');
    }
    listaClientes.value = await respuesta.json();
  } catch (error) {
    console.error('Error al conectar con el backend:', error);
    alert('Error al cargar la lista de clientes.');
  }
};

onMounted(() => {
  cargarClientesDesdeBD();
  const hoy = new Date().toISOString().slice(0, 10);
  formulario.value.fechaCreacion = hoy;
});

const limpiarFormulario = () => {
  formulario.value = {
    clienteId: '',
    fechaCreacion: new Date().toISOString().slice(0, 10)
  };
};

const manejarEnvio = async () => {
  if (!formulario.value.clienteId) {
    alert('Por favor, selecciona un cliente.');
    return;
  }

  const POST_URL = 'http://localhost:8080/api/historias-clinicas';

  const datosAEnviar = {
    cliente: {
      id: Number(formulario.value.clienteId)
    },
    fechaCreacion: `${formulario.value.fechaCreacion}T00:00:00`
  };

  try {
    cargando.value = true;

    const respuesta = await fetch(POST_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(datosAEnviar)
    });

    if (respuesta.ok) {
      const resultado = await respuesta.json();
      alert(`¡Historia clínica creada con éxito! ID del expediente: ${resultado.id}`);
      limpiarFormulario();
    } else {
      const mensajeError = await respuesta.text();
      alert(`Error del sistema: ${mensajeError}`);
    }
  } catch (error) {
    console.error('Error al conectar con el servidor:', error);
    alert('No se pudo conectar con el servidor. Inténtalo de nuevo más tarde.');
  } finally {
    cargando.value = false;
  }
};
</script>

<style scoped>
.historia-container {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  min-height: 100%;
}

.historia-card {
  background: #fff;
  border-radius: 20px;
  padding: 2.5rem 3.5rem;
  width: 100%;
  max-width: 550px;
  box-shadow: 0 8px 30px rgba(29, 158, 117, 0.12);
  border: 1px solid #c8f0e6;
}

.historia-header {
  display: flex;
  align-items: center;
  gap: 1.2rem;
  margin-bottom: 2rem;
}

.historia-logo {
  width: 100px;
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

select,
input[type="date"] {
  width: 100%;
  padding: 12px 18px;
  border: 2px solid #9fe1cb;
  border-radius: 10px;
  font-size: 1rem;
  background: #f8fdfb;
  transition: all 0.3s;
  box-sizing: border-box;
}

select:focus,
input[type="date"]:focus {
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
.btn-clear:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-2px);
}

.btn-create:disabled,
.btn-clear:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}
</style>