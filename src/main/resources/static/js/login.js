/* ===========================================================
   V01 · Inicio de sesion
   El rol se obtiene de la cuenta del usuario, no se elige en pantalla.
   =========================================================== */

const formulario = document.getElementById('formulario');
const cajaError = document.getElementById('error');
const botonEntrar = document.getElementById('boton-entrar');

// Con una sesion abierta se redirige al panel
fetch('/api/auth/sesion').then(r => {
  if (r.ok) window.location.href = 'panel.html';
});

formulario.addEventListener('submit', async (evento) => {
  evento.preventDefault();
  cajaError.hidden = true;

  const usuario = document.getElementById('usuario').value.trim();
  const clave = document.getElementById('clave').value;

  if (!usuario || !clave) {
    mostrarError('Ingrese usuario y contraseña');
    return;
  }

  botonEntrar.disabled = true;
  botonEntrar.textContent = 'Verificando...';

  try {
    await api('/auth/login', 'POST', { usuario, clave });
    window.location.href = 'panel.html';
  } catch (error) {
    // Mensaje generico del servidor para cualquier credencial invalida
    mostrarError(error.message);
    document.getElementById('clave').value = '';
  } finally {
    botonEntrar.disabled = false;
    botonEntrar.textContent = 'Entrar';
  }
});

function mostrarError(texto) {
  cajaError.textContent = texto;
  cajaError.hidden = false;
}
