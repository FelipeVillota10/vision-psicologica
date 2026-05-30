<template>
  <div class="buscar-historia-container">
    <div class="card-busqueda">
      <div class="encabezado">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="logo" />
        <div>
          <h2>Buscar historias clínicas</h2>
          <p>Filtra por cliente y por rango de fechas</p>
        </div>
      </div>

      <form @submit.prevent="aplicarFiltros" class="formulario">
        <div class="campo">
          <label for="clienteFiltro">Cliente</label>
          <input
            id="clienteFiltro"
            v-model="clienteFiltro"
            type="text"
            placeholder="Escriba el nombre del cliente"
          />
        </div>

        <div class="rangos">
          <div class="campo">
            <label for="fechaDesde">Fecha desde</label>
            <input id="fechaDesde" v-model="fechaDesde" type="date" />
          </div>

          <div class="campo">
            <label for="fechaHasta">Fecha hasta</label>
            <input id="fechaHasta" v-model="fechaHasta" type="date" />
          </div>
        </div>

        <div class="botones">
          <button type="submit" class="btn buscar" :disabled="cargando">
            {{ cargando ? 'Cargando...' : 'Buscar' }}
          </button>
          <button type="button" class="btn limpiar" @click="limpiar">
            Limpiar
          </button>
        </div>
      </form>

      <p v-if="mensajeError" class="error">{{ mensajeError }}</p>
      <p v-if="mensajeExito" class="exito">{{ mensajeExito }}</p>

      <div class="tabla-contenedor">
        <h3>Historias clínicas registradas</h3>

        <table class="tabla-historias">
          <thead>
            <tr>
              <th>ID</th>
              <th>Cliente</th>
              <th>Fecha</th>
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="historiasFiltradas.length === 0">
              <td colspan="4" class="sin-datos">
                No hay historias clínicas para mostrar.
              </td>
            </tr>

            <tr v-for="historia in historiasFiltradas" :key="historia.id">
              <td>{{ historia.id ?? 'Sin ID' }}</td>
              <td>{{ historia.cliente?.nombre ?? 'Sin cliente' }}</td>
              <td>{{ formatearFecha(historia.fecha) }}</td>
              <td class="acciones">
                <button type="button" class="btn-accion ver" @click="verHistoria(historia)">
                  Ver
                </button>
                <button type="button" class="btn-accion editar" @click="editarHistoria(historia)">
                  Modificar
                </button>
                <button type="button" class="btn-accion eliminar" @click="eliminarHistoria(historia.id)">
                  Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="historiaSeleccionada" class="resultado">
        <h3>Historia seleccionada</h3>

        <div class="dato">
          <strong>Cliente:</strong>
          <span>{{ historiaSeleccionada.cliente?.nombre ?? 'Sin dato' }}</span>
        </div>

        <div class="dato">
          <strong>Fecha:</strong>
          <span>{{ formatearFecha(historiaSeleccionada.fecha) }}</span>
        </div>

        <div class="dato">
          <strong>ID Historia:</strong>
          <span>{{ historiaSeleccionada.id ?? 'Sin dato' }}</span>
        </div>

        <div class="dato" v-if="historiaSeleccionada.observaciones">
          <strong>Observaciones:</strong>
          <span>{{ historiaSeleccionada.observaciones }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

type Cliente = {
  id?: number
  nombre?: string
}

type HistoriaClinica = {
  id?: number
  fecha?: string
  observaciones?: string
  cliente?: Cliente
}

const router = useRouter()
const historias = ref<HistoriaClinica[]>([])
const clienteFiltro = ref('')
const fechaDesde = ref('')
const fechaHasta = ref('')
const historiaSeleccionada = ref<HistoriaClinica | null>(null)
const mensajeError = ref('')
const mensajeExito = ref('')
const cargando = ref(false)

const cargarHistorias = async () => {
  cargando.value = true
  mensajeError.value = ''
  mensajeExito.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/historias-clinicas')

    if (!response.ok) {
      const errorTexto = await response.text()
      throw new Error(errorTexto || 'No se pudieron cargar las historias clínicas.')
    }

    historias.value = await response.json()
  } catch (error: any) {
    mensajeError.value = error.message || 'Error al cargar las historias clínicas.'
  } finally {
    cargando.value = false
  }
}

onMounted(() => {
  cargarHistorias()
})

const historiasFiltradas = computed(() => {
  return historias.value.filter((historia) => {
    const nombreCliente = historia.cliente?.nombre?.toLowerCase() || ''
    const filtroCliente = clienteFiltro.value.toLowerCase().trim()
    const fechaHistoria = historia.fecha?.slice(0, 10) || ''

    const coincideCliente =
      !filtroCliente || nombreCliente.includes(filtroCliente)

    const coincideDesde =
      !fechaDesde.value || fechaHistoria >= fechaDesde.value

    const coincideHasta =
      !fechaHasta.value || fechaHistoria <= fechaHasta.value

    return coincideCliente && coincideDesde && coincideHasta
  })
})

const aplicarFiltros = () => {
  mensajeError.value = ''
  mensajeExito.value = `Se encontraron ${historiasFiltradas.value.length} historia(s).`
}

const limpiar = () => {
  clienteFiltro.value = ''
  fechaDesde.value = ''
  fechaHasta.value = ''
  historiaSeleccionada.value = null
  mensajeError.value = ''
  mensajeExito.value = ''
}

const formatearFecha = (fecha?: string) => {
  if (!fecha) return 'Sin fecha'
  return fecha.slice(0, 10)
}

const verHistoria = (historia: HistoriaClinica) => {
  historiaSeleccionada.value = historia
}

const editarHistoria = (historia: HistoriaClinica) => {
  if (!historia.id) {
    mensajeError.value = 'No se puede modificar: la historia no tiene un ID válido.'
    return
  }

  router.push({
    name: 'modificar-historia',
    query: { id: historia.id } // Esto pone el ?id=1 en la barra del navegador
  })
}

const eliminarHistoria = async (id?: number) => {
  if (!id) {
    mensajeError.value = 'No se puede eliminar: la historia no tiene ID.'
    return
  }

  const confirmar = confirm('¿Desea eliminar esta historia clínica?')
  if (!confirmar) return

  try {
    const response = await fetch(`http://localhost:8080/api/historias-clinicas/${id}`, {
      method: 'DELETE'
    })

    if (!response.ok) {
      const errorTexto = await response.text()
      throw new Error(errorTexto || 'No se pudo eliminar la historia clínica.')
    }

    mensajeExito.value = 'Historia clínica eliminada correctamente.'
    historiaSeleccionada.value = null
    await cargarHistorias()
  } catch (error: any) {
    mensajeError.value = error.message || 'Error al eliminar la historia clínica.'
  }
}
</script>

<style scoped>
.buscar-historia-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
}

.card-busqueda {
  width: 100%;
  max-width: 1000px;
  background: #ffffff;
  border-radius: 22px;
  padding: 2rem;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
  border: 1px solid #d8ecec;
}

.encabezado {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.logo {
  width: 70px;
  height: 70px;
  object-fit: contain;
}

.encabezado h2 {
  margin: 0;
  font-size: 1.8rem;
  color: #1f7a78;
}

.encabezado p {
  margin: 0.25rem 0 0;
  color: #556;
}

.formulario {
  display: grid;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.campo {
  display: grid;
  gap: 0.45rem;
}

.campo label {
  font-weight: 600;
  color: #1f4f4f;
}

.campo input {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid #9fdad7;
  border-radius: 12px;
  outline: none;
  font-size: 1rem;
  background: #fbffff;
}

.campo input:focus {
  border-color: #5bb7b4;
}

.rangos {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.botones {
  display: flex;
  gap: 1rem;
}

.btn {
  flex: 1;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.buscar {
  background: linear-gradient(90deg, #8fe1df, #5db7b4);
  color: #ffffff;
}

.limpiar {
  background: linear-gradient(90deg, #c0c0f0, #8f8fd8);
  color: #ffffff;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  margin-top: 1rem;
  color: #c62828;
  font-weight: 600;
}

.exito {
  margin-top: 1rem;
  color: #1b8a5a;
  font-weight: 600;
}

.tabla-contenedor {
  margin-top: 1.5rem;
}

.tabla-contenedor h3 {
  margin-bottom: 1rem;
  color: #2f6f6d;
}

.tabla-historias {
  width: 100%;
  border-collapse: collapse;
  background: #f9ffff;
  border-radius: 12px;
  overflow: hidden;
}

.tabla-historias th,
.tabla-historias td {
  border: 1px solid #d8ecec;
  padding: 0.8rem;
  text-align: left;
  vertical-align: middle;
}

.tabla-historias th {
  background: #eaf7f7;
  color: #235f5d;
}

.sin-datos {
  text-align: center;
  padding: 1.2rem;
  color: #666;
}

.acciones {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn-accion {
  border: none;
  border-radius: 10px;
  padding: 0.55rem 0.8rem;
  cursor: pointer;
  font-weight: 600;
}

.ver {
  background: #dceeff;
}

.editar {
  background: #fff2cc;
}

.eliminar {
  background: #ffd6d6;
}

.resultado {
  margin-top: 1.5rem;
  padding: 1.2rem;
  border-radius: 16px;
  background: #f6fbfb;
  border: 1px solid #d8ecec;
}

.resultado h3 {
  margin-top: 0;
  color: #2f6f6d;
}

.dato {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.7rem;
  flex-wrap: wrap;
}

.dato strong {
  min-width: 110px;
}
</style>