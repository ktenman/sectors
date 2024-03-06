import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  root: 'ui',
  plugins: [vue()],
  server: {
    port: 61234,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      },
    },
  },

  build: {
    outDir: '../dist', // This will output the build artifacts to /app/dist when the root is /app/ui
    sourcemap: true,
  },
})
