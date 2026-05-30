<template>
  <div class="modulo-container">
    <div class="modulo-header">
      <h2>Búsqueda de cliente</h2>
      <p class="subtitle">Consulta los datos básicos de los pacientes registrados en el sistema por documento, nombre o email</p>
    </div>

    <div class="search-box">
      <div class="form-group">
        <label for="buscarCliente">Buscar Cliente (Documento, Nombre o Email):</label>
        <div class="search-input-group">
          <input
            id="buscarCliente"
            v-model="terminoBusqueda"
            type="text"
            placeholder="Digite la cédula, el nombre o el correo del paciente..."
            @keyup.enter="ejecutarBusqueda"
          />
          <button class="btn-search" @click="ejecutarBusqueda">Buscar</button>
          <button class="btn-clear" @click="limpiarBusqueda">Limpiar</button>
        </div>
      </div>
    </div>

    <div class="resultados-section">
      <div class="resultados-meta">
        <span>Clientes Encontrados: <strong>{{ clientesFiltrados.length }}</strong></span>
      </div>

      <table class="tabla-datos" v-if="clientesFiltrados.length > 0">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Identificación</th>
            <th>Email</th>
            <th>Dirección</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cliente in clientesFiltrados" :key="cliente.id">
            <td>{{ cliente.nombre }}</td>
            <td class="txt-bold">{{ cliente.identificacion }}</td>
            <td>{{ cliente.correoElectronico }}</td>
            <td>{{ cliente.direccion || 'No registrada' }}</td>
          </tr>
        </tbody>
      </table>

      <div class="no-resultados" v-else>
        <p>No se encontraron registros que coincidan con la búsqueda.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue' // 1. Importamos computed

const terminoBusqueda = ref('')
const listaClientesReal = ref<any[]>([])

// 2. LA MAGIA: clientesFiltrados ahora es un computed.
// Se actualizará solo cada vez que 'terminoBusqueda' cambius o lleguen datos del backend.
const clientesFiltrados = computed(() => {
  const query = terminoBusqueda.value.trim().toLowerCase()

  // Si el buscador está vacío, muestra todos los clientes inmediatamente
  if (!query) {
    return listaClientesReal.value
  }

  // Si hay texto, filtra en tiempo real
  return listaClientesReal.value.filter(cliente => {
    const coincideIdentificacion = cliente.identificacion?.toLowerCase().includes(query)
    const coincideNombre = cliente.nombre?.toLowerCase().includes(query)
    const coincideEmail = cliente.correoElectronico?.toLowerCase().includes(query)
    
    return coincideIdentificacion || coincideNombre || coincideEmail
  })
})

// Función para conectar con tu backend
const obtenerClientesDeBackend = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/clientes')
    if (!response.ok) {
      throw new Error('Error al conectar con el servidor.')
    }
    const data = await response.json()
    listaClientesReal.value = data
  } catch (error) {
    console.error('💥 Error al cargar los clientes:', error)
  }
}

onMounted(async () => {
  await obtenerClientesDeBackend()
})

// 3. El botón de limpiar ahora solo necesita vaciar el texto,
// y la tabla se reestablecerá sola gracias al computed.
const limpiarBusqueda = () => {
  terminoBusqueda.value = ''
}
</script>

<style scoped>
.modulo-container {
  padding: 1.5rem;
  width: 100%;
  box-sizing: border-box;
}

.modulo-header h2 {
  color: #344a73;
  margin: 0 0 0.25rem 0;
  font-size: 1.8rem;
}

.subtitle {
  color: #666;
  margin: 0 0 1.5rem 0;
  font-size: 0.95rem;
}

.search-box {
  background-color: #f4f5f9;
  padding: 1.25rem;
  border-radius: 10px;
  border: 1px solid #c5c6eb;
  margin-bottom: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: 600;
  color: #344a73;
}

.search-input-group {
  display: flex;
  gap: 0.75rem;
}

input {
  flex: 1;
  padding: 10px 14px;
  border: 1.5px solid #b8b9e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

input:focus {
  border-color: #344a73;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-search {
  background-color: #344a73;
  color: white;
}

.btn-search:hover {
  background-color: #253552;
}

.btn-clear {
  background-color: #e2e4f0;
  color: #344a73;
}

.btn-clear:hover {
  background-color: #c5c6eb;
}

.resultados-meta {
  margin-bottom: 0.75rem;
  color: #555;
  font-size: 0.9rem;
}

.txt-bold {
  font-weight: 600;
  color: #344a73;
}

.tabla-datos {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.tabla-datos th, .tabla-datos td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e4f0;
}

.tabla-datos th {
  background-color: #c5c6eb;
  color: #2c2c2c;
  font-weight: 600;
}

.tabla-datos tr:hover {
  background-color: #f9f9fe;
}

.no-resultados {
  padding: 2rem;
  text-align: center;
  background-color: #fafafa;
  border: 1px dashed #b8b9e0;
  border-radius: 8px;
  color: #777;
}
</style>