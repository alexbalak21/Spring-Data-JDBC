import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Proxy API requests to the Spring Boot backend
      '/api': {
        target: 'http://localhost:8080', // Your Spring Boot server
        changeOrigin: true,
        secure: false,
      },
    },
    port: 3000, // Frontend dev server port
  },
  // This will make sure your built files reference paths correctly when served from the Spring Boot server
  base: '/',
  build: {
    outDir: '../src/main/resources/static', // Output to Spring's static resources
    emptyOutDir: true,
  },
})
