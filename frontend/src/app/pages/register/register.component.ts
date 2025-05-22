// Importaciones necesarias para formularios, peticiones HTTP, navegación y módulos esenciales
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule]
})
export class RegisterComponent {
  // Formulario reactivo y variable para errores
  registerForm: FormGroup;
  error: string = '';

  // 🔧 Constructor con inyección de dependencias y creación del formulario
  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.registerForm = this.fb.group({
      userName: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(12)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      name: ['', Validators.required]
    });
  }

  // Método que se ejecuta al enviar el formulario
  onSubmit() {
    this.error = ''; // Limpiar errores anteriores

    // Si el formulario es inválido, marcamos los campos como tocados y salimos
    if (this.registerForm.invalid) {
      Object.keys(this.registerForm.controls).forEach(field => {
        const control = this.registerForm.get(field);
        control?.markAsTouched({ onlySelf: true });
        control?.markAsDirty({ onlySelf: true });
        control?.updateValueAndValidity({ onlySelf: true });
      });
      return;
    }

    // Hacemos la petición POST para registrar el usuario
    this.http.post<any>('http://localhost:8080/api/users/register', this.registerForm.value).subscribe({
      next: (response) => {
        // Alerta de éxito al registrar correctamente
        import('sweetalert2').then(Swal => {
          Swal.default.fire({
            icon: 'success',
            title: '¡Registro exitoso!',
            text: 'Tu cuenta ha sido creada correctamente.',
            confirmButtonText: 'Iniciar sesión',
            confirmButtonColor: '#3085d6'
          }).then(() => {
            // Redirigimos al login tras el registro
            this.router.navigate(['/login']);
          });
        });
      },
      error: (err) => {
        console.error('Error al registrar', err);

        // Manejamos errores del backend y los mostramos como texto
        if (err.error && typeof err.error === 'object') {
          this.error = Object.values(err.error).join(' ');
        } else {
          this.error = 'Hubo un problema al registrar.';
        }
      }
    });
  }
}
