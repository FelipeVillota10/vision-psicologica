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
          />
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
            <th>Acciones</th> </tr>
        </thead>
        <tbody>
          <tr v-for="cliente in clientesFiltrados" :key="cliente.id">
            <td>{{ cliente.nombre }}</td>
            <td class="txt-bold">{{ cliente.identificacion }}</td>
            <td>{{ cliente.correoElectronico }}</td>
            <td>{{ cliente.direccion || 'No registrada' }}</td>
            <td>
              <button class="btn-edit" @click="abrirEdicion(cliente)">Editar</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="no-resultados" v-else>
        <p>No se encontraron registros que coincidan con la búsqueda.</p>
      </div>
    </div>

    <div v-if="clienteEditando" class="modal-overlay">
      <div class="modal-content">
        <h3>Editar Cliente</h3>
        <div class="form-edit">
          <label>Nombre:</label>
          <input v-model="clienteEditando.nombre" />
          <label>Identificación:</label>
          <input v-model="clienteEditando.identificacion" />
          <label>Email:</label>
          <input v-model="clienteEditando.correoElectronico" />
          <label>Dirección:</label>
          <input v-model="clienteEditando.direccion" />
        </div>
        <div class="modal-actions">
          <button class="btn-search" @click="guardarCambios">Guardar</button>
          <button class="btn-clear" @click="clienteEditando = null">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

const terminoBusqueda = ref('')
const listaClientesReal = ref<any[]>([])
const clienteEditando = ref<any | null>(null)

const clientesFiltrados = computed(() => {
  const query = terminoBusqueda.value.trim().toLowerCase()
  if (!query) return listaClientesReal.value
  return listaClientesReal.value.filter(cliente => {
    return cliente.identificacion?.toLowerCase().includes(query) ||
           cliente.nombre?.toLowerCase().includes(query) ||
           cliente.correoElectronico?.toLowerCase().includes(query)
  })
})

const obtenerClientesDeBackend = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/clientes')
    if (!response.ok) throw new Error('Error al conectar')
    listaClientesReal.value = await response.json()
  } catch (error) {
    console.error('Error:', error)
  }
}

const abrirEdicion = (cliente: any) => {
  clienteEditando.value = JSON.parse(JSON.stringify(cliente))
} 

const guardarCambios = async () => {
  try {
    const payload = {
      nombre: clienteEditando.value.nombre,
      identificacion: clienteEditando.value.identificacion,
      correoElectronico: clienteEditando.value.correoElectronico,
      direccion: clienteEditando.value.direccion
    };

    const response = await fetch(`http://localhost:8080/api/clientes/${clienteEditando.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      alert("¡Cliente actualizado con éxito!");
      clienteEditando.value = null; // Cierra el modal
      await obtenerClientesDeBackend(); // Refresca la tabla
    } else {
      const errorText = await response.text();
      alert("Error al actualizar: " + errorText);
    }
  } catch (error) {
    console.error("Error al guardar:", error);
    alert("No se pudo conectar con el servidor.");
  }
}

const limpiarBusqueda = () => terminoBusqueda.value = ''

onMounted(obtenerClientesDeBackend)
</script>

<style scoped>
/* Estilos existentes */
.modulo-container { padding: 1.5rem; }
.modulo-header h2 { color: #344a73; }
.search-box { background-color: #f4f5f9; padding: 1.25rem; border-radius: 10px; margin-bottom: 1.5rem; }
.search-input-group { display: flex; gap: 0.75rem; }
input { flex: 1; padding: 10px; border: 1.5px solid #b8b9e0; border-radius: 8px; }
button { padding: 10px 20px; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; }
.btn-search { background: linear-gradient(90deg, #acadd6, #7e80da); color: white; }
.btn-clear { background: linear-gradient(90deg, #baf5f2, #47a595); color: rgb(253, 253, 253); }
.tabla-datos { width: 100%; border-collapse: collapse; background: white; }
.tabla-datos th, .tabla-datos td { padding: 12px; border-bottom: 1px solid #e2e4f0; text-align: left; }
.btn-edit { background: linear-gradient(90deg, #acadd6, #7e80da) ; color: white; padding: 5px 10px; font-size: 0.8rem; }

/* Nuevos estilos para el Modal */
.modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background: rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center; }
.modal-content { background: white; padding: 2rem; border-radius: 8px; width: 350px; }
.form-edit { display: flex; flex-direction: column; gap: 10px; margin-bottom: 1rem; }
.modal-actions { display: flex; gap: 10px; }
</style>