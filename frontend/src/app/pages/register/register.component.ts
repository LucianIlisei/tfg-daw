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
  registerForm: FormGroup;
  error: string = ''; // ✅ AÑADIDO para que no falle el HTML

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {
    this.registerForm = this.fb.group({
      userName: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(12)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      name: ['', Validators.required]
    });
  }

  onSubmit() {
    this.error = ''; // Reiniciar mensaje de error

    if (this.registerForm.invalid) {
      Object.keys(this.registerForm.controls).forEach(field => {
        const control = this.registerForm.get(field);
        control?.markAsTouched({ onlySelf: true });
        control?.markAsDirty({ onlySelf: true });
        control?.updateValueAndValidity({ onlySelf: true });
      });
      return;
    }

    this.http.post<any>('http://localhost:8080/api/users/register', this.registerForm.value).subscribe({
      next: () => {
        import('sweetalert2').then(Swal => {
          Swal.default.fire({
            icon: 'success',
            title: '¡Registro exitoso!',
            text: 'Tu cuenta ha sido creada correctamente.',
            confirmButtonText: 'Iniciar sesión',
            confirmButtonColor: '#3085d6'
          }).then(() => this.router.navigate(['/login']));
        });
      },
      error: (err) => {
        const mensaje =
          err.error?.error ||
          'Error al registrar el usuario. Verifica si el email o nombre de usuario ya están en uso.';

        this.error = mensaje;

        import('sweetalert2').then(Swal => {
          Swal.default.fire({
            icon: 'error',
            title: 'Registro fallido',
            text: mensaje,
            confirmButtonColor: '#d33'
          });
        });
      }
    });
  }
}
