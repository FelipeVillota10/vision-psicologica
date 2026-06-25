// src/config/api.ts
const API_URL = import.meta.env.VITE_API_URL

if (!API_URL) {
  console.warn('⚠️ VITE_API_URL no está definida. Usando fallback.')
}

export const apiConfig = {
  baseURL: API_URL || 'https://vision-psicologica-backend.vercel.app',
  timeout: 12000,
}

export const getApiUrl = (endpoint: string): string => {
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`
  return `${apiConfig.baseURL}${cleanEndpoint}`
}

// Configuración para fetch (puedes usar axios después si quieres)
export const fetchConfig = {
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
}