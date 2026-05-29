<template>
  <div class="contenedor-pantalla">
    <div class="tarjeta-formulario">
      
      <h2>Crear historia Clínica</h2>
      
      <form @submit.prevent="manejarEnvio">
        
        <div class="campo-grupo">
          <label for="cliente">Cliente *</label>
          <select id="cliente" v-model="formulario.clienteId" required>
            <option value="" disabled selected>Seleccionar</option>
            <option v-for="cliente in listaClientes" :key="cliente.id" :value="cliente.id">
              {{ cliente.nombre }} ({{ cliente.identificacion }})
            </option>
          </select>
        </div>

        <div class="campo-grupo">
          <label for="fecha">Fecha de creación *</label>
          <input 
            type="date" 
            id="fecha" 
            v-model="formulario.fechaCreacion" 
            required
          />
        </div>

        <div class="botones-contenedor">
          <button type="submit" class="btn-crear">Crear</button>
          <button type="button" class="btn-limpiar" @click="limpiarFormulario">Limpiar</button>
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

const API_URL = 'http://localhost:8080/api/clientes';

const cargarClientesDesdeBD = async () => {
  try {
    const respuesta = await fetch(API_URL);
    if (!respuesta.ok) {
      throw new Error('No se pudo obtener la lista de clientes');
    }
    // TypeScript ahora sabe que lo que llega aquí encaja con la interfaz Cliente
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
    fechaCreacion: new Date().toISOString().slice(0, 10) // Cambiado aquí también
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
  }
};
</script>

<style scoped>
.contenedor-pantalla {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
}

.tarjeta-formulario {
  background: #ffffff;
  padding: 30px;
  border-radius: 8px;
  border: 1px solid #cccccc;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  width: 350px;
}

h2 {
  text-align: center;
  color: #333333;
  margin-bottom: 25px;
  font-size: 1.5rem;
}

.campo-grupo {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 8px;
  font-weight: bold;
  color: #444444;
  font-size: 0.9rem;
}

select, input[type="date"] {
  padding: 10px;
  border: 1px solid #aaaaaa;
  border-radius: 4px;
  font-size: 1rem;
  background-color: #fff;
  outline: none;
}

select:focus, input[type="date"]:focus {
  border-color: #007bff;
}

.botones-contenedor {
  display: flex;
  justify-content: space-between;
  margin-top: 25px;
}

button {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  border: none;
  width: 45%;
  transition: background-color 0.2s;
}

.btn-crear {
  background-color: #007bff;
  color: white;
}

.btn-crear:hover {
  background-color: #0056b3;
}

.btn-limpiar {
  background-color: #e0e0e0;
  color: #333333;
}

.btn-limpiar:hover {
  background-color: #cccccc;
}
</style>