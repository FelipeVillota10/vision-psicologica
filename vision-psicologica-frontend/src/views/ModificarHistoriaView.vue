<template>
  <div class="modificar-historia-container">
    <div class="card-modificar">
      <div class="encabezado">
        <img src="@/assets/mariposa.png" alt="Visión Psicológica" class="logo" />
        <div>
          <h2>Modifica una historia</h2>
          <p>Actualiza la fecha y los psicólogos con acceso</p>
        </div>
      </div>

      <div v-if="!historiaActual" class="aviso">
        Selecciona una historia desde la tabla para poder modificarla.
      </div>

      <template v-else>
        <div class="info-historia">
          <strong>Historia seleccionada:</strong>
          <span>
            {{ historiaActual.cliente?.nombre ?? 'Sin cliente' }}
            — ID {{ historiaActual.id ?? 'Sin ID' }}
          </span>
        </div>

        <form @submit.prevent="guardarCambios" class="formulario">
          <div class="campo">
            <label>Psicólogos con acceso</label>

            <div class="lista-usuarios">
              <label
                v-for="usuario in usuarios"
                :key="usuario.id"
                class="usuario-item"
              >
                <input
                  type="checkbox"
                  :value="usuario.id"
                  v-model="usuariosSeleccionados"
                />
                <span>{{ usuario.nombre ?? 'Sin nombre' }}</span>
                <small v-if="usuario.email">({{ usuario.email }})</small>
              </label>

              <div v-if="usuarios.length === 0" class="sin-datos">
                No hay usuarios disponibles.
              </div>
            </div>
          </div>

          <div class="campo">
            <label for="fechaNueva">Fecha nueva</label>
            <input
              id="fechaNueva"
              type="date"
              v-model="fechaNueva"
            />
          </div>

          <div class="botones">
            <button type="submit" class="btn guardar" :disabled="guardando">
              {{ guardando ? 'Guardando...' : 'Guardar' }}
            </button>

            <button type="button" class="btn limpiar" @click="limpiar">
              Limpiar
            </button>
          </div>
        </form>

        <p v-if="mensajeError" class="error">{{ mensajeError }}</p>
        <p v-if="mensajeExito" class="exito">{{ mensajeExito }}</p>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router' // 1. ImportamosuseRoute para leer la URL

type Usuario = {
  id?: number
  nombre?: string
  email?: string
}

type HistoriaClinica = {
  id?: number
  fecha?: string
  cliente?: {
    id?: number
    nombre?: string
  }
}

// 2. Eliminamos las props y lo convertimos en un ref local
const route = useRoute()
const historiaActual = ref<HistoriaClinica | null>(null)

const usuarios = ref<Usuario[]>([])
const usuariosSeleccionados = ref<number[]>([])
const usuariosOriginales = ref<number[]>([])
const fechaNueva = ref('')
const mensajeError = ref('')
const mensajeExito = ref('')
const guardando = ref(false)

// 3. Nueva función para traer la historia clínica individual desde tu backend
const cargarHistoriaActual = async (id: string | number) => {
  try {
    const response = await fetch(`http://localhost:8080/api/historias-clinicas/${id}`)
    if (!response.ok) {
      throw new Error('No se pudo cargar la historia clínica seleccionada.')
    }
    historiaActual.value = await response.json()
  } catch (error: any) {
    mensajeError.value = error.message || 'Error al obtener los datos de la historia.'
  }
}

const cargarUsuarios = async () => {
  try {
    const response = await fetch('http://localhost:8080/usuario')

    if (!response.ok) {
      const errorTexto = await response.text()
      throw new Error(errorTexto || 'No se pudieron cargar los usuarios.')
    }

    const data = await response.json()
    usuarios.value = Array.isArray(data) ? data : []
  } catch (error: any) {
    mensajeError.value = error.message || 'Error al cargar usuarios.'
  }
}

const extraerIdUsuario = (item: any): number | null => {
  return item?.usuario?.id ?? item?.idUsuario ?? item?.usuarioId ?? null
}

const cargarAccesosHistoria = async () => {
  // Cambiado de props.historiaActual a historiaActual.value
  if (!historiaActual.value?.id) return

  try {
    const response = await fetch(
      `http://localhost:8080/usuarios-historias/historia/${historiaActual.value.id}`
    )

    if (!response.ok) {
      usuariosOriginales.value = []
      usuariosSeleccionados.value = []
      return
    }

    const data = await response.json()
    const ids = Array.isArray(data)
      ? data
          .map(extraerIdUsuario)
          .filter((id): id is number => id !== null)
      : []

    usuariosOriginales.value = [...ids]
    usuariosSeleccionados.value = [...ids]
  } catch {
    usuariosOriginales.value = []
    usuariosSeleccionados.value = []
  }
}

const prepararFormulario = async () => {
  mensajeError.value = ''
  mensajeExito.value = ''

  // Cambiado de props.historiaActual a historiaActual.value
  if (historiaActual.value?.fecha) {
    fechaNueva.value = historiaActual.value.fecha.slice(0, 10)
  } else {
    fechaNueva.value = ''
  }

  await cargarAccesosHistoria()
}

// 4. Inicializamos la pantalla obteniendo el ID de los Query Params de la URL
onMounted(async () => {
  await cargarUsuarios()
  
  const idHistoria = route.query.id
  if (idHistoria) {
    await cargarHistoriaActual(idHistoria as string)
    await prepararFormulario()
  } else {
    mensajeError.value = 'No se ha seleccionado ninguna historia clínica válida.'
  }
})

// 5. Escuchamos si cambia el ID en la URL para recargar el formulario
watch(
  () => route.query.id,
  async (nuevoId) => {
    if (nuevoId) {
      await cargarHistoriaActual(nuevoId as string)
      await prepararFormulario()
    } else {
      historiaActual.value = null
    }
  }
)

const guardarCambios = async () => {
  // Cambiado de props.historiaActual a historiaActual.value
  if (!historiaActual.value?.id) {
    mensajeError.value = 'No hay una historia seleccionada.'
    return
  }

  if (!fechaNueva.value) {
    mensajeError.value = 'Debe seleccionar una fecha nueva.'
    return
  }

  guardando.value = true
  mensajeError.value = ''
  mensajeExito.value = ''

  try {
    const historialId = historiaActual.value.id

    const payloadHistoria = {
      ...historiaActual.value,
      fecha: fechaNueva.value
    }

    const respuestaHistoria = await fetch(
      `http://localhost:8080/api/historias-clinicas/${historialId}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payloadHistoria)
      }
    )

    if (!respuestaHistoria.ok) {
      const errorTexto = await respuestaHistoria.text()
      throw new Error(errorTexto || 'No se pudo actualizar la historia clínica.')
    }

    const seleccionados = [...usuariosSeleccionados.value]
    const originales = [...usuariosOriginales.value]

    const aAsignar = seleccionados.filter(id => !originales.includes(id))
    const aRevocar = originales.filter(id => !seleccionados.includes(id))

    for (const idUsuario of aAsignar) {
      const response = await fetch('http://localhost:8080/usuarios-historias/asignar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          usuario: { id: idUsuario },
          historiaClinica: { id: historialId }
        })
      })

      if (!response.ok) {
        const errorTexto = await response.text()
        throw new Error(errorTexto || 'No se pudo asignar acceso a un psicólogo.')
      }
    }

    for (const idUsuario of aRevocar) {
      const response = await fetch(
        `http://localhost:8080/usuarios-historias/revocar?idUsuario=${idUsuario}&idHistoria=${historialId}`,
        {
          method: 'DELETE'
        }
      )

      if (!response.ok) {
        const errorTexto = await response.text()
        throw new Error(errorTexto || 'No se pudo revocar un acceso.')
      }
    }

    usuariosOriginales.value = [...seleccionados]
    mensajeExito.value = 'Historia clínica actualizada correctamente.'
  } catch (error: any) {
    mensajeError.value = error.message || 'Error al guardar los cambios.'
  } finally {
    guardando.value = false
  }
}

const limpiar = async () => {
  mensajeError.value = ''
  mensajeExito.value = ''

  // Cambiado de props.historiaActual a historiaActual.value
  if (historiaActual.value?.fecha) {
    fechaNueva.value = historiaActual.value.fecha.slice(0, 10)
  } else {
    fechaNueva.value = ''
  }

  usuariosSeleccionados.value = [...usuariosOriginales.value]
}
</script>

<style scoped>
.modificar-historia-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 2rem;
}

.card-modificar {
  width: 100%;
  max-width: 920px;
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

.aviso {
  padding: 1rem;
  border-radius: 12px;
  background: #fff7e6;
  border: 1px solid #ffd699;
  color: #8a5a00;
  font-weight: 600;
}

.info-historia {
  margin-bottom: 1rem;
  padding: 0.9rem 1rem;
  border-radius: 12px;
  background: #f6fbfb;
  border: 1px solid #d8ecec;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.formulario {
  display: grid;
  gap: 1rem;
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

.lista-usuarios {
  max-height: 280px;
  overflow-y: auto;
  border: 1px solid #9fdad7;
  border-radius: 12px;
  padding: 0.75rem;
  background: #fbffff;
  display: grid;
  gap: 0.6rem;
}

.usuario-item {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0.55rem 0.5rem;
  border-radius: 10px;
  cursor: pointer;
}

.usuario-item:hover {
  background: #eefbfb;
}

.sin-datos {
  padding: 0.8rem;
  color: #666;
}

.botones {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.btn {
  flex: 1;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.guardar {
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
</style>