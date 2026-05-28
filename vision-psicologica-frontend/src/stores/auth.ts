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
    async iniciarSesion(email: string, contrasena: string): Promise<boolean> {
      try {
        const response = await axios.post('http://localhost:8080/usuario/login', {
          email: email,
          contrasena: contrasena,
        })

        // Si el backend responde con éxito (Status 200)
        if (response.status === 200) {
          return true
        }
        return false
      } catch (error) {
        console.error('Error en las credenciales:', error)
        alert('Email o contraseña incorrectos.')
        return false
      }
    },
  },
})
