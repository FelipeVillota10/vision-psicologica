import { defineStore } from 'pinia'
import axios from 'axios'

interface UsuarioData {
  nombre: string
  identificacion: string
  email: string
  contrasena: string
}

export const useAuthStore = defineStore('auth', {
  actions: {
    async registrarUsuario(usuario: UsuarioData) {
      try {
        const response = await axios.post('http://localhost:8080/usuario/save', usuario)

        if (response.status === 201 || response.status === 200) {
          alert('¡Usuario creado con éxito en la base de datos!')
          return true
        }
      } catch (error) {
        console.error('Error al conectar con el backend:', error)
        alert('Hubo un error al registrar el cliente. Revisa la consola o tu backend.')
        return false
      }
    },
  },
})
